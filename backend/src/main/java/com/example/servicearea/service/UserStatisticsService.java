package com.example.servicearea.service;

import java.util.Map;

public interface UserStatisticsService {

    Map<String, Object> getUserStatistics();

    void clearUserStatisticsCache();
}
