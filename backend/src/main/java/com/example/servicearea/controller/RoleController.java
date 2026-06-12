package com.example.servicearea.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.servicearea.common.ApiResponse;
import com.example.servicearea.entity.Role;
import com.example.servicearea.service.RoleService;
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

/**
 * 角色管理控制器
 */
@Validated
@RestController
@RequestMapping("/admin/roles")
@RequiredArgsConstructor
@Tag(name = "角色管理", description = "角色管理相关接口")
public class RoleController {

    private final RoleService roleService;

    /**
     * 获取所有角色（用于下拉框）
     */
    @Operation(summary = "获取所有角色", description = "获取所有角色，用于下拉框选择")
    @PreAuthorize("hasPermission(null, 'role:list')")
    @GetMapping("/list")
    public ApiResponse<List<Role>> list() {
        List<Role> roles = roleService.listAll();
        return ApiResponse.ok(roles);
    }

    /**
     * 分页查询角色
     */
    @Operation(summary = "分页查询角色", description = "支持角色名称模糊搜索")
    @PreAuthorize("hasPermission(null, 'role:list')")
    @GetMapping("/page")
    public ApiResponse<IPage<Role>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name) {
        Page<Role> page = new Page<>(pageNum, pageSize);
        IPage<Role> result = roleService.queryPage(page, name);
        return ApiResponse.ok(result);
    }

    /**
     * 新增角色
     */
    @Operation(summary = "新增角色", description = "新增角色")
    @PreAuthorize("hasPermission(null, 'role:add')")
    @PostMapping
    public ApiResponse<Void> add(@Valid @RequestBody RoleRequest request) {
        Role role = new Role();
        role.setName(request.getName());
        role.setCode(request.getCode());
        role.setStatus(request.getStatus());

        if (roleService.save(role)) {
            return ApiResponse.ok();
        }
        return ApiResponse.fail(500, "新增失败");
    }

    /**
     * 修改角色
     */
    @Operation(summary = "修改角色", description = "修改角色信息")
    @PreAuthorize("hasPermission(null, 'role:edit')")
    @PutMapping("/{id}")
    public ApiResponse<Void> update(
            @PathVariable Long id,
            @Valid @RequestBody RoleRequest request) {
        Role role = new Role();
        role.setId(id);
        role.setName(request.getName());
        role.setCode(request.getCode());
        role.setStatus(request.getStatus());

        if (roleService.update(role)) {
            return ApiResponse.ok();
        }
        return ApiResponse.fail(500, "修改失败");
    }

    /**
     * 删除角色
     */
    @Operation(summary = "删除角色", description = "删除角色，同时删除角色-权限关联")
    @PreAuthorize("hasPermission(null, 'role:delete')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        if (roleService.delete(id)) {
            return ApiResponse.ok();
        }
        return ApiResponse.fail(500, "删除失败");
    }

    /**
     * 获取角色已分配的权限ID列表
     */
    @Operation(summary = "获取角色权限", description = "获取角色已分配的权限ID列表")
    @PreAuthorize("hasPermission(null, 'role:list')")
    @GetMapping("/{id}/permissions")
    public ApiResponse<List<Long>> getRolePermissions(@PathVariable Long id) {
        List<Long> permissionIds = roleService.getRolePermissionIds(id);
        return ApiResponse.ok(permissionIds);
    }

    /**
     * 分配权限
     */
    @Operation(summary = "分配权限", description = "为角色分配权限")
    @PreAuthorize("hasPermission(null, 'role:assign')")
    @PutMapping("/{id}/permissions")
    public ApiResponse<Void> assignPermissions(
            @PathVariable Long id,
            @Valid @RequestBody AssignPermissionsRequest request) {
        if (roleService.assignPermissions(id, request.getPermissionIds())) {
            return ApiResponse.ok();
        }
        return ApiResponse.fail(500, "分配失败");
    }

    /**
     * 角色请求DTO
     */
    @Data
    public static class RoleRequest {
        @NotBlank(message = "角色名称不能为空")
        private String name;

        @NotBlank(message = "角色编码不能为空")
        private String code;

        private Integer status;
    }

    /**
     * 分配权限请求DTO
     */
    @Data
    public static class AssignPermissionsRequest {
        private List<Long> permissionIds;
    }
}
