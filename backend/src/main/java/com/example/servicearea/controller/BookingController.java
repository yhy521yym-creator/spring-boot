package com.example.servicearea.controller;

import com.example.servicearea.common.ApiResponse;
import com.example.servicearea.entity.Order;
import com.example.servicearea.entity.Reservation;
import com.example.servicearea.service.BookingService;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

/**
 * 用户预约下单（订单+预约同事务），独立路径避免与 /orders/{id} 冲突。
 */
@RestController
@RequestMapping("/bookings")
@Validated
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> create(@RequestBody @Validated BookingRequest request) {
        try {
            Order order = new Order();
            order.setUserId(request.getUserId());
            order.setMerchantId(request.getMerchantId());
            order.setProductId(request.getProductId());
            order.setQuantity(request.getQuantity());
            order.setTotalPrice(request.getTotalPrice());
            order.setReservationDate(request.getReservationDate());
            order.setReservationTime(request.getReservationTime());
            order.setContactName(request.getContactName());
            order.setContactPhone(request.getContactPhone());
            order.setRemark(request.getRemark());

            Reservation reservation = new Reservation();
            reservation.setMerchantId(request.getMerchantId());
            reservation.setUserId(request.getUserId());
            reservation.setUserName(request.getContactName());
            reservation.setUserPhone(request.getContactPhone());
            reservation.setReservationDate(request.getReservationDate());
            reservation.setReservationTime(request.getReservationTime());
            reservation.setDuration(request.getDuration() != null ? request.getDuration() : 60);
            reservation.setQuantity(request.getQuantity());
            reservation.setRemark(request.getReservationRemark());

            return ApiResponse.ok(bookingService.placeOrderWithReservation(order, reservation));
        } catch (RuntimeException e) {
            return ApiResponse.fail(400, e.getMessage());
        }
    }

    @Data
    public static class BookingRequest {
        private Long userId;
        private Long merchantId;
        private Long productId;
        private Integer quantity;
        private BigDecimal totalPrice;
        private LocalDate reservationDate;
        private LocalTime reservationTime;
        private String contactName;
        private String contactPhone;
        private String remark;
        private Integer duration;
        private String reservationRemark;
    }
}
