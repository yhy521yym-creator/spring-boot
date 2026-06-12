package com.example.servicearea.controller;

import com.example.servicearea.common.ApiResponse;
import com.example.servicearea.entity.Permission;
import com.example.servicearea.service.PermissionService;
import com.example.servicearea.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 权限管理控制器
 */
@Validated
@RestController
@RequestMapping("/admin/permissions")
@RequiredArgsConstructor
@Tag(name = "权限管理", description = "权限管理相关接口")
public class PermissionController {

    private final PermissionService permissionService;
    private final UserService userService;

    /**
     * 获取权限树
     */
    @Operation(summary = "获取权限树", description = "获取全部权限树，含按钮节点")
    @PreAuthorize("hasPermission(null, 'permission:list')")
    @GetMapping("/tree")
    public ApiResponse<List<Map<String, Object>>> getPermissionTree() {
        List<Map<String, Object>> tree = permissionService.getPermissionTree();
        return ApiResponse.ok(tree);
    }

    /**
     * 获取当前用户的菜单（动态侧边栏用）
     */
    @Operation(summary = "获取当前用户菜单", description = "获取当前用户有权限的菜单树，用于动态侧边栏渲染")
    @GetMapping("/menus")
    public ApiResponse<List<Map<String, Object>>> getUserMenus() {
        List<Map<String, Object>> menus = permissionService.getUserMenus();
        return ApiResponse.ok(menus);
    }

    /**
     * 新增权限
     */
    @Operation(summary = "新增权限", description = "新增权限")
    @PreAuthorize("hasPermission(null, 'permission:add')")
    @PostMapping
    public ApiResponse<Void> add(@Valid @RequestBody PermissionRequest request) {
        Permission permission = new Permission();
        permission.setName(request.getName());
        permission.setCode(request.getCode());
        permission.setType(request.getType());
        permission.setParentId(request.getParentId());
        permission.setPath(request.getPath());
        permission.setComponent(request.getComponent());
        permission.setIcon(request.getIcon());
        permission.setSort(request.getSort());
        permission.setStatus(request.getStatus());

        if (permissionService.save(permission)) {
            return ApiResponse.ok();
        }
        return ApiResponse.fail(500, "新增失败");
    }

    /**
     * 修改权限
     */
    @Operation(summary = "修改权限", description = "修改权限信息")
    @PreAuthorize("hasPermission(null, 'permission:edit')")
    @PutMapping("/{id}")
    public ApiResponse<Void> update(
            @PathVariable Long id,
            @Valid @RequestBody PermissionRequest request) {
        Permission permission = new Permission();
        permission.setId(id);
        permission.setName(request.getName());
        permission.setCode(request.getCode());
        permission.setType(request.getType());
        permission.setParentId(request.getParentId());
        permission.setPath(request.getPath());
        permission.setComponent(request.getComponent());
        permission.setIcon(request.getIcon());
        permission.setSort(request.getSort());
        permission.setStatus(request.getStatus());

        if (permissionService.update(permission)) {
            return ApiResponse.ok();
        }
        return ApiResponse.fail(500, "修改失败");
    }

    /**
     * 删除权限
     */
    @Operation(summary = "删除权限", description = "删除权限，级联删除子权限")
    @PreAuthorize("hasPermission(null, 'permission:delete')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        if (permissionService.delete(id)) {
            return ApiResponse.ok();
        }
        return ApiResponse.fail(500, "删除失败");
    }

    /**
     * 权限请求DTO
     */
    @Data
    public static class PermissionRequest {
        @NotBlank(message = "权限名称不能为空")
        private String name;

        @NotBlank(message = "权限编码不能为空")
        private String code;

        @NotBlank(message = "权限类型不能为空")
        private String type;

        private Long parentId;

        private String path;

        private String component;

        private String icon;

        private Integer sort;

        private Integer status;
    }
}