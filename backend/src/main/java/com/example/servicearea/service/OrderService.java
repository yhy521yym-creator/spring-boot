package com.example.servicearea.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.servicearea.entity.Order;
import java.util.List;

public interface OrderService {
    IPage<Order> getPage(int current, int size, Long userId, Long merchantId, String status, Long serviceAreaId);
    List<Order> getList(Long userId, Long merchantId, String status, Long serviceAreaId);
    Order getById(Long id);
    Order getByOrderNo(String orderNo);
    boolean save(Order order);
    boolean update(Order order);
    boolean updateStatus(Long id, String status);

    Order simulatePay(Long id, String payMethod);

    String generateOrderNo();
    java.util.Map<String, Object> getStatistics();
}
