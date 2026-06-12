package com.example.servicearea.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.servicearea.entity.SysUser;
import com.example.servicearea.mapper.SysUserMapper;
import com.example.servicearea.service.UserStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserStatisticsServiceImpl implements UserStatisticsService {

    private final SysUserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String USER_STATISTICS_CACHE_KEY = "statistics:user:statistics";

    @Override
    public Map<String, Object> getUserStatistics() {
        try {
            Object cachedResult = redisTemplate.opsForValue().get(USER_STATISTICS_CACHE_KEY);
            if (cachedResult != null) {
                return (Map<String, Object>) cachedResult;
            }
        } catch (Exception e) {
            log.error("Redis缓存获取失败，使用数据库数据", e);
        }

        Map<String, Object> result = new HashMap<>();

        List<SysUser> allUsers = userMapper.selectList(null);
        long totalUsers = allUsers.size();
        long activeUsers = allUsers.stream().filter(u -> u.getStatus() != null && u.getStatus() == 1).count();

        result.put("total", totalUsers);
        result.put("active", activeUsers);

        try {
            redisTemplate.opsForValue().set(USER_STATISTICS_CACHE_KEY, result, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("Redis缓存设置失败", e);
        }

        return result;
    }

    @Override
    public void clearUserStatisticsCache() {
        try {
            redisTemplate.delete(USER_STATISTICS_CACHE_KEY);
            log.info("Cleared user statistics cache");
        } catch (Exception e) {
            log.error("Failed to clear user statistics cache", e);
        }
    }
}
