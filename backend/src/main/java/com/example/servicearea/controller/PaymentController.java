package com.example.servicearea.controller;

import com.example.servicearea.common.ApiResponse;
import com.example.servicearea.entity.Order;
import com.example.servicearea.service.OrderService;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

/**
 * 模拟支付（独立路径，避免与静态资源或订单路由冲突）
 */
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final OrderService orderService;

    public PaymentController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/order/{orderId:\\d+}")
    public ApiResponse<Order> payOrder(
            @PathVariable Long orderId,
            @RequestBody(required = false) PayRequest request) {
        try {
            String method = request != null ? request.getPayMethod() : "SIMULATE";
            return ApiResponse.ok(orderService.simulatePay(orderId, method));
        } catch (RuntimeException e) {
            return ApiResponse.fail(400, e.getMessage());
        }
    }

    @Data
    public static class PayRequest {
        private String payMethod;
    }
}
