package com.example.servicearea.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.servicearea.entity.Order;
import com.example.servicearea.entity.OrderOperationLog;

import java.util.List;

public interface OrderOperationLogService {

    void recordStatusChange(Order order, String oldStatus, String newStatus, String remark);

    List<OrderOperationLog> listByOrderId(Long orderId);

    IPage<OrderOperationLog> getPage(int current, int size, Long orderId, String orderNo);
}
