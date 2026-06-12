package com.example.servicearea.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.servicearea.entity.Merchant;
import com.example.servicearea.entity.Order;
import com.example.servicearea.util.FacilitiesParser;
import com.example.servicearea.entity.Product;
import com.example.servicearea.entity.Reservation;
import com.example.servicearea.entity.ServiceArea;
import com.example.servicearea.entity.SysUser;
import com.example.servicearea.entity.VisitLog;
import com.example.servicearea.mapper.MerchantMapper;
import com.example.servicearea.mapper.OrderMapper;
import com.example.servicearea.mapper.ProductMapper;
import com.example.servicearea.mapper.ReservationMapper;
import com.example.servicearea.mapper.ServiceAreaMapper;
import com.example.servicearea.mapper.VisitLogMapper;
import com.example.servicearea.security.DataScopeHelper;
import com.example.servicearea.security.SecurityUtils;
import com.example.servicearea.service.StatisticsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 统计分析服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final ServiceAreaMapper serviceAreaMapper;
    private final VisitLogMapper visitLogMapper;
    private final OrderMapper orderMapper;
    private final MerchantMapper merchantMapper;
    private final com.example.servicearea.mapper.SysUserMapper sysUserMapper;
    private final ReservationMapper reservationMapper;
    private final ProductMapper productMapper;
    private final DataScopeHelper dataScopeHelper;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Map<String, Object> getMyOverview() {
        SysUser user = dataScopeHelper.requireCurrentUser();
        Map<String, Object> result = new HashMap<>();
        if (SecurityUtils.isPlatformAdmin()) {
            return getOverview();
        }
        if (SecurityUtils.isEndUser()) {
            long orderCount = orderMapper.selectCount(
                    new LambdaQueryWrapper<Order>().eq(Order::getUserId, user.getId()));
            long reservationCount = reservationMapper.selectCount(
                    new LambdaQueryWrapper<Reservation>().eq(Reservation::getUserId, user.getId()));
            result.put("myOrderCount", orderCount);
            result.put("myReservationCount", reservationCount);
            result.put("role", "USER");
            return result;
        }
        if (SecurityUtils.isMerchant() && user.getMerchantId() != null) {
            Long merchantId = user.getMerchantId();
            long orderCount = orderMapper.selectCount(
                    new LambdaQueryWrapper<Order>().eq(Order::getMerchantId, merchantId));
            long pendingOrders = orderMapper.selectCount(
                    new LambdaQueryWrapper<Order>()
                            .eq(Order::getMerchantId, merchantId)
                            .eq(Order::getStatus, "PENDING"));
            long reservationCount = reservationMapper.selectCount(
                    new LambdaQueryWrapper<Reservation>().eq(Reservation::getMerchantId, merchantId));
            long productCount = productMapper.selectCount(
                    new LambdaQueryWrapper<Product>().eq(Product::getMerchantId, merchantId));
            result.put("myOrderCount", orderCount);
            result.put("pendingOrderCount", pendingOrders);
            result.put("myReservationCount", reservationCount);
            result.put("myProductCount", productCount);
            result.put("merchantId", merchantId);
            result.put("role", "MERCHANT");
            return result;
        }
        result.put("role", user.getRole());
        return result;
    }

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> result = new HashMap<>();
        // 统计服务区总数（只读不修改）
        result.put("serviceAreaCount", serviceAreaMapper.selectCount(null));
        // 统计商户总数（只读不修改）
        result.put("merchantCount", merchantMapper.selectCount(null));
        // 统计订单总数（只读不修改）
        result.put("orderCount", orderMapper.selectCount(null));
        // 统计用户总数（只读不修改）
        result.put("userCount", sysUserMapper.selectCount(null));
        return result;
    }

    @Override
    public List<Map<String, Object>> getServiceAreaCountByRegion() {
        String cacheKey = "statistics:region:count";

        try {
            Object cachedResult = redisTemplate.opsForValue().get(cacheKey);
            if (cachedResult != null) {
                return (List<Map<String, Object>>) cachedResult;
            }
        } catch (Exception e) {
            log.error("Redis缓存获取失败，使用数据库数据", e);
        }

        List<ServiceArea> serviceAreas = serviceAreaMapper.selectList(null);

        Map<String, Integer> regionCountMap = new HashMap<>();
        for (ServiceArea area : serviceAreas) {
            String region = area.getRegion();
            if (region == null || region.isEmpty()) {
                region = "未分配";
            }
            regionCountMap.put(region, regionCountMap.getOrDefault(region, 0) + 1);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : regionCountMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("region", entry.getKey());
            item.put("count", entry.getValue());
            result.add(item);
        }

        try {
            redisTemplate.opsForValue().set(cacheKey, result, 1, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("Redis缓存设置失败", e);
        }

        return result;
    }

    @Override
    public Map<String, Integer> getFacilitiesDistribution() {
        String cacheKey = "statistics:facilities:distribution";

        try {
            Object cachedResult = redisTemplate.opsForValue().get(cacheKey);
            if (cachedResult != null) {
                return (Map<String, Integer>) cachedResult;
            }
        } catch (Exception e) {
            log.error("Redis缓存获取失败，使用数据库数据", e);
        }

        List<ServiceArea> serviceAreas = serviceAreaMapper.selectList(null);

        Map<String, Integer> facilitiesCountMap = new HashMap<>();
        for (ServiceArea area : serviceAreas) {
            for (String facility : FacilitiesParser.parse(area.getFacilities())) {
                facilitiesCountMap.put(facility, facilitiesCountMap.getOrDefault(facility, 0) + 1);
            }
        }

        try {
            redisTemplate.opsForValue().set(cacheKey, facilitiesCountMap, 1, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("Redis缓存设置失败", e);
        }

        return facilitiesCountMap;
    }

    @Override
    public List<Map<String, Object>> getServiceAreaVisits() {
        String cacheKey = "statistics:service:area:visits";

        try {
            Object cachedResult = redisTemplate.opsForValue().get(cacheKey);
            if (cachedResult != null) {
                return (List<Map<String, Object>>) cachedResult;
            }
        } catch (Exception e) {
            log.error("Redis缓存获取失败，使用数据库数据", e);
        }

        List<ServiceArea> serviceAreas = serviceAreaMapper.selectList(null);

        LambdaQueryWrapper<VisitLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(VisitLog::getServiceAreaId);
        List<VisitLog> visitLogs = visitLogMapper.selectList(wrapper);

        Map<Long, Long> visitCountMap = visitLogs.stream()
                .collect(Collectors.groupingBy(VisitLog::getServiceAreaId, Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (ServiceArea area : serviceAreas) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", area.getId());
            item.put("name", area.getName());
            item.put("visits", visitCountMap.getOrDefault(area.getId(), 0L).intValue());
            result.add(item);
        }

        result.sort((a, b) -> Integer.compare((Integer) b.get("visits"), (Integer) a.get("visits")));

        try {
            redisTemplate.opsForValue().set(cacheKey, result, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("Redis缓存设置失败", e);
        }

        return result;
    }

    @Override
    public Map<String, Object> getTodayVisits() {
        String cacheKey = "statistics:today:visits";
        
        try {
            Object cachedResult = redisTemplate.opsForValue().get(cacheKey);
            if (cachedResult != null) {
                return (Map<String, Object>) cachedResult;
            }
        } catch (Exception e) {
            log.error("Redis缓存获取失败，使用数据库数据", e);
        }

        Map<String, Object> result = new HashMap<>();

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        LambdaQueryWrapper<VisitLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(VisitLog::getVisitTime, startOfDay, endOfDay);
        wrapper.select(VisitLog::getServiceAreaId);
        List<VisitLog> todayVisits = visitLogMapper.selectList(wrapper);

        long totalVisits = todayVisits.size();

        Map<Long, Long> visitCountMap = todayVisits.stream()
                .collect(Collectors.groupingBy(VisitLog::getServiceAreaId, Collectors.counting()));

        result.put("total", totalVisits);
        result.put("byServiceArea", visitCountMap);

        try {
            redisTemplate.opsForValue().set(cacheKey, result, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("Redis缓存设置失败", e);
        }

        return result;
    }

    @Override
    public Map<String, Object> getOrderStatistics() {
        String cacheKey = "statistics:order:overview";

        try {
            Object cachedResult = redisTemplate.opsForValue().get(cacheKey);
            if (cachedResult != null) {
                return (Map<String, Object>) cachedResult;
            }
        } catch (Exception e) {
            log.error("Redis缓存获取失败，使用数据库数据", e);
        }

        Map<String, Object> result = new HashMap<>();

        List<Order> allOrders = orderMapper.selectList(null);

        long totalOrders = allOrders.size();
        long pendingOrders = allOrders.stream().filter(o -> "PENDING".equals(o.getStatus())).count();
        long confirmedOrders = allOrders.stream().filter(o -> "CONFIRMED".equals(o.getStatus())).count();
        long completedOrders = allOrders.stream().filter(o -> "COMPLETED".equals(o.getStatus())).count();
        long cancelledOrders = allOrders.stream().filter(o -> "CANCELLED".equals(o.getStatus())).count();

        double totalAmount = allOrders.stream()
                .filter(o -> o.getTotalPrice() != null)
                .mapToDouble(o -> o.getTotalPrice().doubleValue())
                .sum();

        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        long todayOrders = allOrders.stream()
                .filter(o -> o.getCreateTime() != null && o.getCreateTime().isAfter(startOfToday))
                .count();

        result.put("totalOrders", totalOrders);
        result.put("pendingOrders", pendingOrders);
        result.put("confirmedOrders", confirmedOrders);
        result.put("completedOrders", completedOrders);
        result.put("cancelledOrders", cancelledOrders);
        result.put("totalAmount", totalAmount);
        result.put("todayOrders", todayOrders);

        try {
            redisTemplate.opsForValue().set(cacheKey, result, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("Redis缓存设置失败", e);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getOrderTrend(int days) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.between(Order::getCreateTime, startOfDay, endOfDay);
            List<Order> orders = orderMapper.selectList(wrapper);

            long orderCount = orders.size();
            double totalAmount = orders.stream()
                    .filter(o -> o.getTotalPrice() != null)
                    .mapToDouble(o -> o.getTotalPrice().doubleValue())
                    .sum();

            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.toString());
            dayData.put("orderCount", orderCount);
            dayData.put("totalAmount", totalAmount);
            result.add(dayData);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getOrderStatisticsByMerchant() {
        String cacheKey = "statistics:order:byMerchant";

        try {
            Object cachedResult = redisTemplate.opsForValue().get(cacheKey);
            if (cachedResult != null) {
                return (List<Map<String, Object>>) cachedResult;
            }
        } catch (Exception e) {
            log.error("Redis缓存获取失败，使用数据库数据", e);
        }

        List<Order> orders = orderMapper.selectList(null);
        List<Merchant> merchants = merchantMapper.selectList(null);

        Map<Long, Map<String, Object>> merchantStatsMap = new HashMap<>();
        for (Merchant merchant : merchants) {
            Map<String, Object> stats = new HashMap<>();
            stats.put("merchantId", merchant.getId());
            stats.put("merchantName", merchant.getName());
            stats.put("orderCount", 0L);
            stats.put("totalAmount", 0.0);
            merchantStatsMap.put(merchant.getId(), stats);
        }

        for (Order order : orders) {
            Long merchantId = order.getMerchantId();
            if (merchantId != null && merchantStatsMap.containsKey(merchantId)) {
                Map<String, Object> stats = merchantStatsMap.get(merchantId);
                stats.put("orderCount", (Long) stats.get("orderCount") + 1);
                if (order.getTotalPrice() != null) {
                    stats.put("totalAmount", (Double) stats.get("totalAmount") + order.getTotalPrice().doubleValue());
                }
            }
        }

        List<Map<String, Object>> result = new ArrayList<>(merchantStatsMap.values());
        result.sort((a, b) -> Long.compare((Long) b.get("orderCount"), (Long) a.get("orderCount")));

        try {
            redisTemplate.opsForValue().set(cacheKey, result, 1, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("Redis缓存设置失败", e);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getOrderStatisticsByServiceArea() {
        String cacheKey = "statistics:order:byServiceArea";

        try {
            Object cachedResult = redisTemplate.opsForValue().get(cacheKey);
            if (cachedResult != null) {
                return (List<Map<String, Object>>) cachedResult;
            }
        } catch (Exception e) {
            log.error("Redis缓存获取失败，使用数据库数据", e);
        }

        List<Order> orders = orderMapper.selectList(null);
        List<Merchant> merchants = merchantMapper.selectList(null);
        List<ServiceArea> serviceAreas = serviceAreaMapper.selectList(null);

        Map<Long, String> merchantToServiceAreaMap = new HashMap<>();
        for (Merchant merchant : merchants) {
            merchantToServiceAreaMap.put(merchant.getId(), merchant.getServiceAreaId().toString());
        }

        Map<Long, Map<String, Object>> serviceAreaStatsMap = new HashMap<>();
        for (ServiceArea area : serviceAreas) {
            Map<String, Object> stats = new HashMap<>();
            stats.put("serviceAreaId", area.getId());
            stats.put("serviceAreaName", area.getName());
            stats.put("orderCount", 0L);
            stats.put("totalAmount", 0.0);
            serviceAreaStatsMap.put(area.getId(), stats);
        }

        for (Order order : orders) {
            Long merchantId = order.getMerchantId();
            if (merchantId != null) {
                String serviceAreaIdStr = merchantToServiceAreaMap.get(merchantId);
                if (serviceAreaIdStr != null) {
                    Long serviceAreaId = Long.parseLong(serviceAreaIdStr);
                    if (serviceAreaStatsMap.containsKey(serviceAreaId)) {
                        Map<String, Object> stats = serviceAreaStatsMap.get(serviceAreaId);
                        stats.put("orderCount", (Long) stats.get("orderCount") + 1);
                        if (order.getTotalPrice() != null) {
                            stats.put("totalAmount", (Double) stats.get("totalAmount") + order.getTotalPrice().doubleValue());
                        }
                    }
                }
            }
        }

        List<Map<String, Object>> result = new ArrayList<>(serviceAreaStatsMap.values());
        result.sort((a, b) -> Long.compare((Long) b.get("orderCount"), (Long) a.get("orderCount")));

        try {
            redisTemplate.opsForValue().set(cacheKey, result, 1, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("Redis缓存设置失败", e);
        }

        return result;
    }
}
