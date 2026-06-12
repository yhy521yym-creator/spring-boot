package com.example.servicearea.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.servicearea.common.ApiResponse;
import com.example.servicearea.entity.SiteConfig;
import com.example.servicearea.service.SiteConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 站点配置控制器
 */
@Validated
@RestController
@RequestMapping("/admin/config")
@RequiredArgsConstructor
@Tag(name = "站点配置", description = "站点配置相关接口")
public class SiteConfigController {

    private final SiteConfigService siteConfigService;

    /**
     * 分页查询配置
     */
    @Operation(summary = "分页查询配置", description = "支持配置键名模糊搜索")
    @PreAuthorize("hasPermission(null, 'config:list')")
    @GetMapping("/list")
    public ApiResponse<IPage<SiteConfig>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String configKey) {
        Page<SiteConfig> page = new Page<>(pageNum, pageSize);
        IPage<SiteConfig> result = siteConfigService.queryPage(page, configKey);
        return ApiResponse.ok(result);
    }

    /**
     * 根据键获取配置值（公开接口）
     */
    @Operation(summary = "根据键获取配置值", description = "公开接口，无需权限")
    @GetMapping("/{key}")
    public ApiResponse<String> getValueByKey(@PathVariable String key) {
        String value = siteConfigService.getValueByKey(key);
        return ApiResponse.ok(value);
    }

    /**
     * 新增或更新配置
     */
    @Operation(summary = "新增或更新配置", description = "若key存在则更新，否则新增")
    @PreAuthorize("hasPermission(null, 'config:add')")
    @PostMapping
    public ApiResponse<Void> saveOrUpdate(@Valid @RequestBody SiteConfigRequest request) {
        SiteConfig config = new SiteConfig();
        config.setConfigKey(request.getConfigKey());
        config.setConfigValue(request.getConfigValue());
        config.setDescription(request.getDescription());
        config.setStatus(request.getStatus());

        if (siteConfigService.saveOrUpdate(config)) {
            return ApiResponse.ok();
        }
        return ApiResponse.fail(500, "操作失败");
    }

    /**
     * 删除配置
     */
    @Operation(summary = "删除配置", description = "删除指定ID的配置")
    @PreAuthorize("hasPermission(null, 'config:delete')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        if (siteConfigService.delete(id)) {
            return ApiResponse.ok();
        }
        return ApiResponse.fail(500, "删除失败");
    }

    /**
     * 获取地图API Key（公开接口）
     */
    @Operation(summary = "获取地图API Key", description = "快捷获取地图API Key，公开接口")
    @GetMapping("/public/map-key")
    public ApiResponse<String> getMapKey() {
        String mapKey = siteConfigService.getMapKey();
        return ApiResponse.ok(mapKey);
    }

    /**
     * 站点配置请求DTO
     */
    @Data
    public static class SiteConfigRequest {
        @NotBlank(message = "配置键名不能为空")
        private String configKey;

        private String configValue;

        private String description;

        private Integer status;
    }
}
