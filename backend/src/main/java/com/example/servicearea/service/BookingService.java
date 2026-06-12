package com.example.servicearea.service;

import com.example.servicearea.entity.Order;
import com.example.servicearea.entity.Reservation;

import java.util.Map;

public interface BookingService {

    /**
     * 在同一事务中创建订单并关联预约，任一步失败则回滚（含库存扣减）。
     */
    Map<String, Object> placeOrderWithReservation(Order order, Reservation reservation);
}
