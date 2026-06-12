package com.example.servicearea.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.servicearea.common.ApiResponse;
import com.example.servicearea.entity.ServiceArea;
import com.example.servicearea.entity.VisitLog;
import com.example.servicearea.mapper.VisitLogMapper;
import com.example.servicearea.service.ServiceAreaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequestMapping("/service-areas")
@RequiredArgsConstructor
public class ServiceAreaController {

    private static final Logger logger = LoggerFactory.getLogger(ServiceAreaController.class);
    private final ServiceAreaService serviceAreaService;
    private final VisitLogMapper visitLogMapper;

    @GetMapping
    public ApiResponse<IPage<ServiceArea>> list(
            Authentication authentication,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String address) {
        logger.info("ServiceAreaController.list called, auth: {}", authentication);
        if (authentication != null) {
            logger.info("Principal: {}", authentication.getPrincipal());
            logger.info("Authorities: {}", authentication.getAuthorities());
        }
        Page<ServiceArea> page = new Page<>(pageNum, pageSize);
        IPage<ServiceArea> result = serviceAreaService.queryPage(page, name, address);
        logger.info("Returning {} records", result.getRecords().size());
        return ApiResponse.ok(result);
    }

    /**
     * 获取服务区详情
     * 所有登录用户可访问
     * 同时记录访问日志
     */
    @GetMapping("/{id}")
    public ApiResponse<ServiceArea> getById(@PathVariable Long id, HttpServletRequest request) {
        ServiceArea serviceArea = serviceAreaService.getById(id);
        if (serviceArea == null) {
            return ApiResponse.fail(404, "服务区不存在");
        }

        recordVisitLog(id, request);
        return ApiResponse.ok(serviceArea);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 新增服务区
     * 仅管理员可访问
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR')")
    public ApiResponse<Void> add(@Valid @RequestBody ServiceAreaRequest request) {
        ServiceArea serviceArea = convertToEntity(request);
        boolean success = serviceAreaService.addServiceArea(serviceArea);
        if (success) {
            return ApiResponse.ok();
        }
        return ApiResponse.fail(500, "新增失败");
    }

    /**
     * 修改服务区
     * 仅管理员可访问
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR')")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody ServiceAreaRequest request) {
        ServiceArea serviceArea = convertToEntity(request);
        serviceArea.setId(id);
        boolean success = serviceAreaService.updateServiceArea(serviceArea);
        if (success) {
            return ApiResponse.ok();
        }
        return ApiResponse.fail(500, "修改失败");
    }

    /**
     * 删除服务区
     * 仅管理员可访问
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        boolean success = serviceAreaService.deleteServiceArea(id);
        if (success) {
            return ApiResponse.ok();
        }
        return ApiResponse.fail(500, "删除失败");
    }

    /**
     * 查询附近的服务区
     * 所有登录用户可访问
     */
    @GetMapping("/nearby")
    public ApiResponse<List<ServiceArea>> nearby(
            @RequestParam @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal lat,
            @RequestParam @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal lng,
            @RequestParam @NotNull @Min(1) @Max(200000) Integer radius,
            HttpServletRequest request) {

        List<ServiceArea> list = serviceAreaService.findNearby(lat, lng, radius);
        for (ServiceArea area : list) {
            if (area.getId() != null) {
                recordVisitLog(area.getId(), request);
            }
        }
        return ApiResponse.ok(list);
    }

    private void recordVisitLog(Long serviceAreaId, HttpServletRequest request) {
        try {
            VisitLog log = new VisitLog();
            log.setServiceAreaId(serviceAreaId);
            log.setIpAddress(getClientIp(request));
            log.setVisitTime(LocalDateTime.now());
            log.setCreateTime(LocalDateTime.now());
            visitLogMapper.insert(log);
        } catch (Exception e) {
            logger.warn("Failed to record visit log for service area {}: {}", serviceAreaId, e.getMessage());
        }
    }

    /**
     * 将请求对象转换为实体
     */
    private ServiceArea convertToEntity(ServiceAreaRequest request) {
        ServiceArea serviceArea = new ServiceArea();
        serviceArea.setName(request.name());
        serviceArea.setAddress(request.address());
        serviceArea.setRegion(request.region());
        serviceArea.setLongitude(request.longitude());
        serviceArea.setLatitude(request.latitude());
        serviceArea.setFacilities(request.facilities());
        serviceArea.setDescription(request.description());
        serviceArea.setStatus(request.status());
        return serviceArea;
    }

    /**
     * 服务区请求DTO（使用record简化）
     */
    public record ServiceAreaRequest(
            @NotNull(message = "名称不能为空") String name,

            @NotNull(message = "地址不能为空") String address,

            String region,

            @NotNull(message = "经度不能为空") @DecimalMin(value = "-180.0", message = "经度范围必须在-180到180之间") @DecimalMax(value = "180.0", message = "经度范围必须在-180到180之间") BigDecimal longitude,

            @NotNull(message = "纬度不能为空") @DecimalMin(value = "-90.0", message = "纬度范围必须在-90到90之间") @DecimalMax(value = "90.0", message = "纬度范围必须在-90到90之间") BigDecimal latitude,

            String facilities,

            String description,

            Integer status) {
    }
}
