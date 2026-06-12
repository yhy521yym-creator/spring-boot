package com.example.servicearea.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.servicearea.entity.Order;
import com.example.servicearea.entity.Reservation;
import com.example.servicearea.mapper.OrderMapper;
import com.example.servicearea.mapper.ReservationMapper;
import com.example.servicearea.entity.SysUser;
import com.example.servicearea.security.DataScopeHelper;
import com.example.servicearea.security.SecurityUtils;
import com.example.servicearea.service.OrderService;
import com.example.servicearea.service.ReservationService;
import com.example.servicearea.support.OrderReservationSyncContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationMapper reservationMapper;
    private final OrderMapper orderMapper;
    private final OrderService orderService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final DataScopeHelper dataScopeHelper;
    
    private static final String RESERVATION_STATISTICS_CACHE_KEY = "statistics:reservation:statistics";
    
    private void clearReservationStatisticsCache() {
        try {
            redisTemplate.delete(RESERVATION_STATISTICS_CACHE_KEY);
            log.info("Cleared reservation statistics cache");
        } catch (Exception e) {
            log.error("Failed to clear reservation statistics cache", e);
        }
    }

    @Override
    public IPage<Reservation> getPage(int current, int size, Long merchantId, String status, Long userId) {
        Page<Reservation> page = new Page<>(current, size);
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        Long scopedMerchantId = dataScopeHelper.scopedMerchantId(merchantId);
        Long scopedUserId = dataScopeHelper.scopedUserId(userId);
        if (scopedMerchantId != null) {
            wrapper.eq(Reservation::getMerchantId, scopedMerchantId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Reservation::getStatus, status);
        }
        if (scopedUserId != null) {
            wrapper.eq(Reservation::getUserId, scopedUserId);
        }
        wrapper.orderByDesc(Reservation::getCreateTime);
        return reservationMapper.selectPage(page, wrapper);
    }

    @Override
    public List<Reservation> getList(Long merchantId, String status, Long userId) {
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        Long scopedMerchantId = dataScopeHelper.scopedMerchantId(merchantId);
        Long scopedUserId = dataScopeHelper.scopedUserId(userId);
        if (scopedMerchantId != null) {
            wrapper.eq(Reservation::getMerchantId, scopedMerchantId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Reservation::getStatus, status);
        }
        if (scopedUserId != null) {
            wrapper.eq(Reservation::getUserId, scopedUserId);
        }
        wrapper.orderByDesc(Reservation::getCreateTime);
        return reservationMapper.selectList(wrapper);
    }

    @Override
    public Reservation getById(Long id) {
        Reservation r = reservationMapper.selectById(id);
        if (r != null) {
            assertCanAccess(r);
        }
        return r;
    }

    @Override
    public boolean save(Reservation reservation) {
        SysUser current = dataScopeHelper.requireCurrentUser();
        if (SecurityUtils.isEndUser()) {
            reservation.setUserId(current.getId());
            if (reservation.getUserName() == null) {
                reservation.setUserName(current.getUsername());
            }
            if (reservation.getUserPhone() == null) {
                reservation.setUserPhone(current.getPhone());
            }
        }
        bindOrderIfPresent(reservation);
        if (reservation.getStatus() == null || reservation.getStatus().isEmpty()) {
            reservation.setStatus("PENDING");
        }
        boolean result = reservationMapper.insert(reservation) > 0;
        if (result) {
            clearReservationStatisticsCache();
        }
        return result;
    }

    @Override
    public boolean update(Reservation reservation) {
        Reservation existing = reservationMapper.selectById(reservation.getId());
        if (existing != null) {
            assertCanAccess(existing);
        }
        boolean result = reservationMapper.updateById(reservation) > 0;
        if (result) {
            clearReservationStatisticsCache();
        }
        return result;
    }

    @Override
    public boolean delete(Long id) {
        Reservation existing = reservationMapper.selectById(id);
        if (existing != null) {
            assertCanAccess(existing);
        }
        boolean result = reservationMapper.deleteById(id) > 0;
        if (result) {
            clearReservationStatisticsCache();
        }
        return result;
    }

    @Override
    @Transactional
    public boolean updateStatus(Long id, String status) {
        Reservation existing = reservationMapper.selectById(id);
        if (existing == null) {
            return false;
        }
        assertCanAccess(existing);
        assertValidReservationStatus(status);

        Reservation reservation = new Reservation();
        reservation.setId(id);
        reservation.setStatus(status);
        boolean result = reservationMapper.updateById(reservation) > 0;
        if (result) {
            clearReservationStatisticsCache();
            syncLinkedOrderStatus(existing, status);
        }
        return result;
    }

    private void bindOrderIfPresent(Reservation reservation) {
        if (reservation.getOrderId() == null) {
            return;
        }
        Order order = orderMapper.selectById(reservation.getOrderId());
        if (order == null) {
            throw new IllegalArgumentException("关联订单不存在");
        }
        if (reservation.getUserId() != null && !reservation.getUserId().equals(order.getUserId())) {
            throw new IllegalArgumentException("订单与预约用户不一致");
        }
        if (reservation.getMerchantId() != null && !reservation.getMerchantId().equals(order.getMerchantId())) {
            throw new IllegalArgumentException("订单与预约商户不一致");
        }
        if (reservation.getUserId() == null) {
            reservation.setUserId(order.getUserId());
        }
    }

    private Long resolveOrderId(Reservation reservation) {
        if (reservation.getOrderId() != null) {
            return reservation.getOrderId();
        }
        if (reservation.getUserId() == null || reservation.getMerchantId() == null) {
            return null;
        }
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, reservation.getUserId())
                .eq(Order::getMerchantId, reservation.getMerchantId());
        if (reservation.getReservationDate() != null) {
            wrapper.eq(Order::getReservationDate, reservation.getReservationDate());
        }
        wrapper.orderByDesc(Order::getCreateTime).last("LIMIT 1");
        Order order = orderMapper.selectOne(wrapper);
        return order != null ? order.getId() : null;
    }

    private void syncLinkedOrderStatus(Reservation reservation, String status) {
        Long orderId = resolveOrderId(reservation);
        if (orderId == null || OrderReservationSyncContext.isSyncing()) {
            return;
        }
        Order order = orderMapper.selectById(orderId);
        if (order == null || status.equals(order.getStatus())) {
            return;
        }
        OrderReservationSyncContext.run(() -> {
            try {
                orderService.updateStatus(orderId, status);
            } catch (Exception e) {
                log.warn("预约状态同步至订单失败, reservationId={}, orderId={}", reservation.getId(), orderId, e);
            }
        });
    }

    private void assertValidReservationStatus(String status) {
        if (!List.of("PENDING", "CONFIRMED", "COMPLETED", "CANCELLED").contains(status)) {
            throw new IllegalArgumentException("无效的预约状态");
        }
    }

    @Override
    public Map<String, Object> getStatistics() {
        try {
            Object cachedResult = redisTemplate.opsForValue().get(RESERVATION_STATISTICS_CACHE_KEY);
            if (cachedResult != null) {
                return (Map<String, Object>) cachedResult;
            }
        } catch (Exception e) {
            log.error("Redis缓存获取失败，使用数据库数据", e);
        }

        Map<String, Object> result = new HashMap<>();
        List<Reservation> all = reservationMapper.selectList(null);
        long pending = all.stream().filter(r -> "PENDING".equals(r.getStatus())).count();
        long confirmed = all.stream().filter(r -> "CONFIRMED".equals(r.getStatus())).count();
        long cancelled = all.stream().filter(r -> "CANCELLED".equals(r.getStatus())).count();
        long completed = all.stream().filter(r -> "COMPLETED".equals(r.getStatus())).count();
        result.put("pending", pending);
        result.put("confirmed", confirmed);
        result.put("cancelled", cancelled);
        result.put("completed", completed);
        result.put("total", all.size());

        try {
            redisTemplate.opsForValue().set(RESERVATION_STATISTICS_CACHE_KEY, result, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("Redis缓存设置失败", e);
        }

        return result;
    }

    private void assertCanAccess(Reservation reservation) {
        SysUser current = dataScopeHelper.requireCurrentUser();
        if (SecurityUtils.isPlatformAdmin()) {
            return;
        }
        if (SecurityUtils.isEndUser() && current.getId().equals(reservation.getUserId())) {
            return;
        }
        if (SecurityUtils.isMerchant()
                && current.getMerchantId() != null
                && current.getMerchantId().equals(reservation.getMerchantId())) {
            return;
        }
        SecurityUtils.deny("无权访问该预约");
    }
}
