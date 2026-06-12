package com.example.servicearea.service.impl;

import com.example.servicearea.entity.Order;
import com.example.servicearea.entity.Reservation;
import com.example.servicearea.service.BookingService;
import com.example.servicearea.service.OrderService;
import com.example.servicearea.service.ReservationService;
import com.example.servicearea.support.MerchantAccessHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final OrderService orderService;
    private final ReservationService reservationService;
    private final MerchantAccessHelper merchantAccessHelper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> placeOrderWithReservation(Order order, Reservation reservation) {
        if (order.getMerchantId() != null) {
            merchantAccessHelper.assertMerchantAllowsBooking(order.getMerchantId());
        }
        if (order.getOrderNo() == null || order.getOrderNo().isEmpty()) {
            order.setOrderNo(orderService.generateOrderNo());
        }
        if (order.getStatus() == null || order.getStatus().isEmpty()) {
            order.setStatus("PENDING");
        }
        if (order.getPayStatus() == null || order.getPayStatus().isEmpty()) {
            order.setPayStatus("UNPAID");
        }
        if (!orderService.save(order)) {
            throw new RuntimeException("下单失败");
        }
        if (order.getId() == null) {
            throw new RuntimeException("订单创建异常");
        }

        reservation.setOrderId(order.getId());
        if (!reservationService.save(reservation)) {
            throw new RuntimeException("创建预约失败");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", order.getOrderNo());
        result.put("orderId", order.getId());
        result.put("reservationId", reservation.getId());
        return result;
    }
}
