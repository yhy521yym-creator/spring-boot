package com.example.servicearea.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.servicearea.common.ApiResponse;
import com.example.servicearea.entity.OrderOperationLog;
import com.example.servicearea.service.OrderOperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderOperationLogController {

    private final OrderOperationLogService orderOperationLogService;

    @GetMapping("/orders/{orderId}/logs")
    public ApiResponse<List<OrderOperationLog>> listByOrder(@PathVariable Long orderId) {
        return ApiResponse.ok(orderOperationLogService.listByOrderId(orderId));
    }

    @GetMapping("/order-logs/page")
    public ApiResponse<IPage<OrderOperationLog>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) String orderNo) {
        return ApiResponse.ok(orderOperationLogService.getPage(current, size, orderId, orderNo));
    }
}
