package com.example.servicearea.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.servicearea.entity.ServiceArea;
import com.example.servicearea.mapper.ServiceAreaMapper;
import com.example.servicearea.service.ServiceAreaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * 服务区信息服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceAreaServiceImpl extends ServiceImpl<ServiceAreaMapper, ServiceArea> implements ServiceAreaService {

    private final ServiceAreaMapper serviceAreaMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final List<String> STATISTICS_CACHE_KEYS = Arrays.asList(
        "statistics:region:count",
        "statistics:facilities:distribution",
        "statistics:service:area:visits"
    );
    
    private void clearStatisticsCache() {
        try {
            for (String key : STATISTICS_CACHE_KEYS) {
                redisTemplate.delete(key);
                log.info("Cleared cache key: {}", key);
            }
        } catch (Exception e) {
            log.error("Failed to clear statistics cache", e);
        }
    }

    @Override
    public IPage<ServiceArea> queryPage(Page<ServiceArea> page, String name, String address) {
        return serviceAreaMapper.selectPageWithSearch(page, name, address);
    }

    @Override
    public List<ServiceArea> findNearby(BigDecimal lat, BigDecimal lng, Integer radius) {
        // 参数校验
        if (lat == null || lng == null || radius == null) {
            throw new IllegalArgumentException("经纬度和半径不能为空");
        }
        if (lat.compareTo(new BigDecimal("-90")) < 0 || lat.compareTo(new BigDecimal("90")) > 0) {
            throw new IllegalArgumentException("纬度范围必须在-90到90之间");
        }
        if (lng.compareTo(new BigDecimal("-180")) < 0 || lng.compareTo(new BigDecimal("180")) > 0) {
            throw new IllegalArgumentException("经度范围必须在-180到180之间");
        }
        if (radius <= 0 || radius > 200000) {
            throw new IllegalArgumentException("半径范围必须在1到200000米之间");
        }

        return serviceAreaMapper.selectNearby(lat, lng, radius);
    }

    @Override
    public boolean addServiceArea(ServiceArea serviceArea) {
        // 参数校验
        validateServiceArea(serviceArea);

        // 设置默认状态为正常
        if (serviceArea.getStatus() == null) {
            serviceArea.setStatus(1);
        }

        boolean success = save(serviceArea);
        if (success) {
            clearStatisticsCache();
        }
        return success;
    }

    @Override
    public boolean updateServiceArea(ServiceArea serviceArea) {
        if (serviceArea.getId() == null) {
            throw new IllegalArgumentException("服务区ID不能为空");
        }

        // 参数校验
        validateServiceArea(serviceArea);

        boolean success = updateById(serviceArea);
        if (success) {
            clearStatisticsCache();
        }
        return success;
    }

    @Override
    public boolean deleteServiceArea(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("服务区ID不能为空");
        }
        boolean success = removeById(id);
        if (success) {
            clearStatisticsCache();
        }
        return success;
    }

    /**
     * 校验服务区信息
     */
    private void validateServiceArea(ServiceArea serviceArea) {
        if (serviceArea.getName() == null || serviceArea.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("服务区名称不能为空");
        }
        if (serviceArea.getAddress() == null || serviceArea.getAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("地址不能为空");
        }
        if (serviceArea.getLongitude() == null) {
            throw new IllegalArgumentException("经度不能为空");
        }
        if (serviceArea.getLatitude() == null) {
            throw new IllegalArgumentException("纬度不能为空");
        }

        // 校验经纬度范围
        BigDecimal longitude = serviceArea.getLongitude();
        BigDecimal latitude = serviceArea.getLatitude();

        if (latitude.compareTo(new BigDecimal("-90")) < 0 || latitude.compareTo(new BigDecimal("90")) > 0) {
            throw new IllegalArgumentException("纬度范围必须在-90到90之间");
        }
        if (longitude.compareTo(new BigDecimal("-180")) < 0 || longitude.compareTo(new BigDecimal("180")) > 0) {
            throw new IllegalArgumentException("经度范围必须在-180到180之间");
        }
    }
}
