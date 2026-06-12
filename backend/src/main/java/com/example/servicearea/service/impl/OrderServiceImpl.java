package com.example.servicearea.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.servicearea.entity.Order;
import com.example.servicearea.entity.Product;
import com.example.servicearea.entity.Reservation;
import com.example.servicearea.mapper.OrderMapper;
import com.example.servicearea.mapper.ProductMapper;
import com.example.servicearea.mapper.ReservationMapper;
import com.example.servicearea.entity.SysUser;
import com.example.servicearea.security.DataScopeHelper;
import com.example.servicearea.security.SecurityUtils;
import com.example.servicearea.service.OrderOperationLogService;
import com.example.servicearea.service.OrderService;
import com.example.servicearea.support.MerchantAccessHelper;
import com.example.servicearea.support.OrderReservationSyncContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final ReservationMapper reservationMapper;
    private final com.example.servicearea.mapper.MerchantMapper merchantMapper;
    private final DataScopeHelper dataScopeHelper;
    private final OrderOperationLogService orderOperationLogService;
    private final MerchantAccessHelper merchantAccessHelper;

    @Override
    public IPage<Order> getPage(int current, int size, Long userId, Long merchantId, String status, Long serviceAreaId) {
        Page<Order> page = new Page<>(current, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        Long scopedUserId = dataScopeHelper.scopedUserId(userId);
        Long scopedMerchantId = dataScopeHelper.scopedMerchantId(merchantId);
        if (scopedUserId != null) {
            wrapper.eq(Order::getUserId, scopedUserId);
        }
        if (scopedMerchantId != null) {
            wrapper.eq(Order::getMerchantId, scopedMerchantId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Order::getStatus, status);
        }
        if (serviceAreaId != null && SecurityUtils.isPlatformAdmin()) {
            List<Long> merchantIds = getMerchantIdsByServiceAreaId(serviceAreaId);
            if (!merchantIds.isEmpty()) {
                wrapper.in(Order::getMerchantId, merchantIds);
            } else {
                wrapper.eq(Order::getId, -1L);
            }
        }
        wrapper.orderByDesc(Order::getCreateTime);
        return orderMapper.selectPage(page, wrapper);
    }

    @Override
    public List<Order> getList(Long userId, Long merchantId, String status, Long serviceAreaId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        Long scopedUserId = dataScopeHelper.scopedUserId(userId);
        Long scopedMerchantId = dataScopeHelper.scopedMerchantId(merchantId);
        if (scopedUserId != null) {
            wrapper.eq(Order::getUserId, scopedUserId);
        }
        if (scopedMerchantId != null) {
            wrapper.eq(Order::getMerchantId, scopedMerchantId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Order::getStatus, status);
        }
        if (serviceAreaId != null && SecurityUtils.isPlatformAdmin()) {
            List<Long> merchantIds = getMerchantIdsByServiceAreaId(serviceAreaId);
            if (!merchantIds.isEmpty()) {
                wrapper.in(Order::getMerchantId, merchantIds);
            } else {
                wrapper.eq(Order::getId, -1L);
            }
        }
        wrapper.orderByDesc(Order::getCreateTime);
        return orderMapper.selectList(wrapper);
    }
    
    private void assertCanAccess(Order order) {
        SysUser current = dataScopeHelper.requireCurrentUser();
        if (SecurityUtils.isPlatformAdmin()) {
            return;
        }
        if (SecurityUtils.isEndUser() && current.getId().equals(order.getUserId())) {
            return;
        }
        if (SecurityUtils.isMerchant()
                && current.getMerchantId() != null
                && current.getMerchantId().equals(order.getMerchantId())) {
            return;
        }
        SecurityUtils.deny("无权访问该订单");
    }

    private void assertCanChangeStatus(Order order, String newStatus) {
        if (SecurityUtils.isEndUser()) {
            if (!"CANCELLED".equals(newStatus)) {
                SecurityUtils.deny("普通用户仅可取消自己的订单");
            }
            if (!"PENDING".equals(order.getStatus()) && !"CONFIRMED".equals(order.getStatus())) {
                SecurityUtils.deny("当前订单状态不可取消");
            }
            return;
        }
        if (SecurityUtils.isMerchant() || SecurityUtils.isPlatformAdmin()) {
            if ("CONFIRMED".equals(newStatus) && !isPaid(order)) {
                SecurityUtils.deny("用户尚未支付，无法确认订单");
            }
            if ("CANCELLED".equals(newStatus) || "CONFIRMED".equals(newStatus) || "COMPLETED".equals(newStatus)) {
                return;
            }
            SecurityUtils.deny("无权执行该订单操作");
        }
    }

    private boolean isPaid(Order order) {
        return "PAID".equals(order.getPayStatus());
    }

    private List<Long> getMerchantIdsByServiceAreaId(Long serviceAreaId) {
        LambdaQueryWrapper<com.example.servicearea.entity.Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(com.example.servicearea.entity.Merchant::getServiceAreaId, serviceAreaId);
        List<com.example.servicearea.entity.Merchant> merchants = merchantMapper.selectList(wrapper);
        List<Long> ids = new java.util.ArrayList<>();
        for (com.example.servicearea.entity.Merchant m : merchants) {
            ids.add(m.getId());
        }
        return ids;
    }

    @Override
    public Order getById(Long id) {
        Order order = orderMapper.selectById(id);
        if (order != null) {
            assertCanAccess(order);
        }
        return order;
    }

    @Override
    public Order getByOrderNo(String orderNo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo);
        return orderMapper.selectOne(wrapper);
    }

    @Override
    @Transactional
    public boolean save(Order order) {
        if (order.getOrderNo() == null || order.getOrderNo().isEmpty()) {
            order.setOrderNo(generateOrderNo());
        }
        SysUser current = dataScopeHelper.requireCurrentUser();
        if (SecurityUtils.isEndUser()) {
            order.setUserId(current.getId());
        }
        if (order.getPayStatus() == null || order.getPayStatus().isEmpty()) {
            order.setPayStatus("UNPAID");
        }
        if (SecurityUtils.isEndUser() && order.getMerchantId() != null) {
            merchantAccessHelper.assertMerchantAllowsBooking(order.getMerchantId());
        }

        Product product = productMapper.selectById(order.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        if (product.getStock() == null || product.getStock() < order.getQuantity()) {
            throw new RuntimeException("库存不足，无法下单");
        }
        
        product.setStock(product.getStock() - order.getQuantity());
        productMapper.updateById(product);
        
        return orderMapper.insert(order) > 0;
    }

    @Override
    public boolean update(Order order) {
        return orderMapper.updateById(order) > 0;
    }

    @Override
    @Transactional
    public boolean updateStatus(Long id, String status) {
        Order existingOrder = orderMapper.selectById(id);
        if (existingOrder == null) {
            return false;
        }
        assertCanAccess(existingOrder);
        assertCanChangeStatus(existingOrder, status);

        String oldStatus = existingOrder.getStatus();
        Order order = new Order();
        order.setId(id);
        order.setStatus(status);
        boolean result = orderMapper.updateById(order) > 0;

        if (result) {
            existingOrder.setStatus(status);
            String remark = statusRemark(oldStatus, status);
            orderOperationLogService.recordStatusChange(existingOrder, oldStatus, status, remark);
            if ("CANCELLED".equals(status)) {
                restoreStock(existingOrder);
            }
            syncLinkedReservationStatus(id, status);
        }

        return result;
    }

    private void syncLinkedReservationStatus(Long orderId, String status) {
        if (OrderReservationSyncContext.isSyncing()) {
            return;
        }
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reservation::getOrderId, orderId);
        List<Reservation> reservations = reservationMapper.selectList(wrapper);
        if (reservations.isEmpty()) {
            return;
        }
        OrderReservationSyncContext.run(() -> {
            for (Reservation r : reservations) {
                if (status.equals(r.getStatus())) {
                    continue;
                }
                Reservation upd = new Reservation();
                upd.setId(r.getId());
                upd.setStatus(status);
                reservationMapper.updateById(upd);
            }
        });
    }

    private String statusRemark(String oldStatus, String newStatus) {
        return switch (newStatus) {
            case "CONFIRMED" -> "订单已确认";
            case "COMPLETED" -> "订单已完成";
            case "CANCELLED" -> "订单已取消";
            default -> "状态变更：" + oldStatus + " -> " + newStatus;
        };
    }
    
    private void restoreStock(Order order) {
        if (order.getProductId() != null && order.getQuantity() != null) {
            Product product = productMapper.selectById(order.getProductId());
            if (product != null && product.getStock() != null) {
                product.setStock(product.getStock() + order.getQuantity());
                productMapper.updateById(product);
            }
        }
    }

    @Override
    @Transactional
    public Order simulatePay(Long id, String payMethod) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        assertCanAccess(order);
        if (!SecurityUtils.isEndUser()) {
            SecurityUtils.deny("仅下单用户可支付");
        }
        SysUser current = dataScopeHelper.requireCurrentUser();
        if (!current.getId().equals(order.getUserId())) {
            SecurityUtils.deny("无权支付该订单");
        }
        if ("CANCELLED".equals(order.getStatus())) {
            throw new RuntimeException("订单已取消，无法支付");
        }
        if (isPaid(order)) {
            throw new RuntimeException("订单已支付，请勿重复支付");
        }

        String method = normalizePayMethod(payMethod);
        Order upd = new Order();
        upd.setId(id);
        upd.setPayStatus("PAID");
        upd.setPayTime(LocalDateTime.now());
        upd.setPayMethod(method);
        orderMapper.updateById(upd);

        order.setPayStatus("PAID");
        order.setPayTime(upd.getPayTime());
        order.setPayMethod(method);
        String methodLabel = payMethodLabel(method);
        orderOperationLogService.recordStatusChange(order, order.getStatus(), order.getStatus(),
                "模拟支付成功（" + methodLabel + "），金额 ¥" + order.getTotalPrice());
        return order;
    }

    private String normalizePayMethod(String payMethod) {
        if (payMethod == null || payMethod.isBlank()) {
            return "SIMULATE";
        }
        String m = payMethod.trim().toUpperCase();
        if ("WECHAT".equals(m) || "ALIPAY".equals(m) || "SIMULATE".equals(m)) {
            return m;
        }
        return "SIMULATE";
    }

    private String payMethodLabel(String method) {
        return switch (method) {
            case "WECHAT" -> "微信支付";
            case "ALIPAY" -> "支付宝";
            case "SIMULATE" -> "模拟钱包";
            default -> "模拟支付";
        };
    }

    @Override
    public String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "ORD" + timestamp + uuid;
    }

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> result = new HashMap<>();
        List<Order> allOrders = orderMapper.selectList(null);
        
        long pending = allOrders.stream().filter(o -> "PENDING".equals(o.getStatus())).count();
        long confirmed = allOrders.stream().filter(o -> "CONFIRMED".equals(o.getStatus())).count();
        long cancelled = allOrders.stream().filter(o -> "CANCELLED".equals(o.getStatus())).count();
        long completed = allOrders.stream().filter(o -> "COMPLETED".equals(o.getStatus())).count();
        long unpaid = allOrders.stream().filter(o -> "UNPAID".equals(o.getPayStatus())).count();
        long paidCount = allOrders.stream().filter(this::isPaid).count();

        result.put("PENDING", pending);
        result.put("CONFIRMED", confirmed);
        result.put("CANCELLED", cancelled);
        result.put("COMPLETED", completed);
        result.put("UNPAID", unpaid);
        result.put("PAID", paidCount);
        result.put("total", allOrders.size());
        
        return result;
    }
}
