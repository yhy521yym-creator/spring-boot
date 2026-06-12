package com.example.servicearea.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.servicearea.common.ApiResponse;
import com.example.servicearea.entity.Order;
import com.example.servicearea.entity.Reservation;
import com.example.servicearea.service.BookingService;
import com.example.servicearea.service.OrderService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;
    private final BookingService bookingService;

    @GetMapping("/page")
    public ApiResponse<IPage<Order>> getPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long serviceAreaId) {
        return ApiResponse.ok(orderService.getPage(current, size, userId, merchantId, status, serviceAreaId));
    }

    @GetMapping
    public ApiResponse<IPage<Order>> getList(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long serviceAreaId) {
        return ApiResponse.ok(orderService.getPage(1, 100, userId, merchantId, status, serviceAreaId));
    }

    @GetMapping("/statistics")
    public ApiResponse<java.util.Map<String, Object>> getStatistics() {
        return ApiResponse.ok(orderService.getStatistics());
    }

    @GetMapping("/{id:\\d+}")
    public ApiResponse<Order> getById(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return ApiResponse.fail(404, "订单不存在");
        }
        return ApiResponse.ok(order);
    }

    @GetMapping("/no/{orderNo}")
    public ApiResponse<Order> getByOrderNo(@PathVariable String orderNo) {
        Order order = orderService.getByOrderNo(orderNo);
        if (order == null) {
            return ApiResponse.fail(404, "订单不存在");
        }
        return ApiResponse.ok(order);
    }

    /**
     * 用户预约下单：订单与预约同一事务，避免仅创建订单未创建预约。
     */
    @PostMapping("/book-with-reservation")
    public ApiResponse<java.util.Map<String, Object>> bookWithReservation(
            @RequestBody @Validated BookWithReservationRequest request) {
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

    @PostMapping
    public ApiResponse<java.util.Map<String, Object>> save(@RequestBody @Validated OrderRequest request) {
        try {
            Order order = new Order();
            order.setOrderNo(orderService.generateOrderNo());
            order.setUserId(request.getUserId());
            order.setMerchantId(request.getMerchantId());
            order.setProductId(request.getProductId());
            order.setQuantity(request.getQuantity());
            order.setTotalPrice(request.getTotalPrice());
            order.setStatus("PENDING");
            order.setReservationDate(request.getReservationDate());
            order.setReservationTime(request.getReservationTime());
            order.setContactName(request.getContactName());
            order.setContactPhone(request.getContactPhone());
            order.setRemark(request.getRemark());
            if (!orderService.save(order)) {
                return ApiResponse.fail(500, "下单失败");
            }
            return ApiResponse.ok(java.util.Map.of(
                    "orderNo", order.getOrderNo(),
                    "orderId", order.getId() != null ? order.getId() : 0
            ));
        } catch (RuntimeException e) {
            return ApiResponse.fail(400, e.getMessage());
        }
    }

    @PutMapping("/{id:\\d+}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @RequestBody @Validated OrderRequest request) {
        Order order = new Order();
        order.setId(id);
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
        return ApiResponse.ok(orderService.update(order));
    }

    @PutMapping("/{id:\\d+}/status")
    public ApiResponse<Boolean> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ApiResponse.ok(orderService.updateStatus(id, status));
    }

    /**
     * 用户模拟支付订单
     */
    @PostMapping("/{id:\\d+}/pay")
    public ApiResponse<Order> pay(@PathVariable Long id, @RequestBody(required = false) PayRequest request) {
        try {
            String method = request != null ? request.getPayMethod() : "SIMULATE";
            return ApiResponse.ok(orderService.simulatePay(id, method));
        } catch (RuntimeException e) {
            return ApiResponse.fail(400, e.getMessage());
        }
    }

    @DeleteMapping("/{id:\\d+}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.ok(orderService.updateStatus(id, "CANCELLED"));
    }

    @Data
    public static class OrderRequest {
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
    }

    @Data
    public static class PayRequest {
        private String payMethod;
    }

    @Data
    public static class BookWithReservationRequest {
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
