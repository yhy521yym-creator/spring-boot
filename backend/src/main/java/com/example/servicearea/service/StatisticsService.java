package com.example.servicearea.service;

import java.util.List;
import java.util.Map;

/**
 * 统计分析服务接口
 */
public interface StatisticsService {

    /**
     * 获取概览统计（服务区、商户、订单、用户总数）
     * 
     * @return 概览统计数据
     */
    Map<String, Object> getOverview();

    /**
     * 当前登录用户/商户的概览（普通用户、商户首页用）
     */
    Map<String, Object> getMyOverview();

    /**
     * 按区域统计服务区数量
     * 
     * @return 区域统计结果
     */
    List<Map<String, Object>> getServiceAreaCountByRegion();

    /**
     * 统计设施分布
     * 
     * @return 设施分布统计
     */
    Map<String, Integer> getFacilitiesDistribution();

    /**
     * 服务区访问量排行
     * 
     * @return 访问量排行
     */
    List<Map<String, Object>> getServiceAreaVisits();

    /**
     * 获取今日访问量统计
     * 
     * @return 今日访问量统计
     */
    Map<String, Object> getTodayVisits();

    /**
     * 获取订单统计概览
     * 
     * @return 订单统计概览
     */
    Map<String, Object> getOrderStatistics();

    /**
     * 获取订单时间趋势
     * 
     * @param days 天数
     * @return 订单趋势数据
     */
    List<Map<String, Object>> getOrderTrend(int days);

    /**
     * 按商户统计订单
     * 
     * @return 商户订单统计
     */
    List<Map<String, Object>> getOrderStatisticsByMerchant();

    /**
     * 按服务区统计订单
     * 
     * @return 服务区订单统计
     */
    List<Map<String, Object>> getOrderStatisticsByServiceArea();
}
