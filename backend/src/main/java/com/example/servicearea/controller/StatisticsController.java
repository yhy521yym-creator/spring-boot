package com.example.servicearea.controller;

import com.example.servicearea.common.ApiResponse;
import com.example.servicearea.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 统计分析控制器
 */
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 获取概览统计（服务区、商户、订单、用户总数）
     */
    @GetMapping("/overview")
    @PreAuthorize("hasAuthority('statistics:view') or hasAuthority('*')")
    public ApiResponse<Map<String, Object>> getOverview() {
        Map<String, Object> result = statisticsService.getOverview();
        return ApiResponse.ok(result);
    }

    /**
     * 按区域统计服务区数量
     */
    @GetMapping("/my-overview")
    public ApiResponse<Map<String, Object>> getMyOverview() {
        return ApiResponse.ok(statisticsService.getMyOverview());
    }

    @GetMapping("/service-area-count-by-region")
    @PreAuthorize("hasAuthority('statistics:view') or hasAuthority('*')")
    public ApiResponse<List<Map<String, Object>>> getServiceAreaCountByRegion() {
        List<Map<String, Object>> result = statisticsService.getServiceAreaCountByRegion();
        return ApiResponse.ok(result);
    }

    /**
     * 统计设施分布
     */
    @GetMapping("/facilities-distribution")
    public ApiResponse<Map<String, Integer>> getFacilitiesDistribution() {
        Map<String, Integer> result = statisticsService.getFacilitiesDistribution();
        return ApiResponse.ok(result);
    }

    /**
     * 服务区访问量排行
     */
    @GetMapping("/service-area-visits")
    public ApiResponse<List<Map<String, Object>>> getServiceAreaVisits() {
        List<Map<String, Object>> result = statisticsService.getServiceAreaVisits();
        return ApiResponse.ok(result);
    }

    /**
     * 获取今日访问量统计
     */
    @GetMapping("/today-visits")
    public ApiResponse<Map<String, Object>> getTodayVisits() {
        Map<String, Object> result = statisticsService.getTodayVisits();
        return ApiResponse.ok(result);
    }

    /**
     * 获取订单统计概览
     */
    @GetMapping("/orders")
    public ApiResponse<Map<String, Object>> getOrderStatistics() {
        Map<String, Object> result = statisticsService.getOrderStatistics();
        return ApiResponse.ok(result);
    }

    /**
     * 获取订单时间趋势
     */
    @GetMapping("/orders/trend")
    public ApiResponse<List<Map<String, Object>>> getOrderTrend(
            @RequestParam(defaultValue = "7") int days) {
        List<Map<String, Object>> result = statisticsService.getOrderTrend(days);
        return ApiResponse.ok(result);
    }

    /**
     * 按商户统计订单
     */
    @GetMapping("/orders/merchant")
    public ApiResponse<List<Map<String, Object>>> getOrderStatisticsByMerchant() {
        List<Map<String, Object>> result = statisticsService.getOrderStatisticsByMerchant();
        return ApiResponse.ok(result);
    }

    /**
     * 按服务区统计订单
     */
    @GetMapping("/orders/service-area")
    public ApiResponse<List<Map<String, Object>>> getOrderStatisticsByServiceArea() {
        List<Map<String, Object>> result = statisticsService.getOrderStatisticsByServiceArea();
        return ApiResponse.ok(result);
    }
}
