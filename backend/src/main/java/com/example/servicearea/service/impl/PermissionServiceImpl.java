package com.example.servicearea.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.servicearea.entity.Permission;
import com.example.servicearea.entity.RolePermission;
import com.example.servicearea.entity.SysPermission;
import com.example.servicearea.entity.SysUser;
import com.example.servicearea.entity.UserPermission;
import com.example.servicearea.entity.UserRole;
import com.example.servicearea.mapper.PermissionMapper;
import com.example.servicearea.mapper.RolePermissionMapper;
import com.example.servicearea.mapper.SysPermissionMapper;
import com.example.servicearea.mapper.UserPermissionMapper;
import com.example.servicearea.mapper.UserRoleMapper;
import com.example.servicearea.service.PermissionService;
import com.example.servicearea.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限服务实现类
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionMapper permissionMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final UserPermissionMapper userPermissionMapper;
    private final UserRoleMapper userRoleMapper;
    private final UserService userService;
    private final SysPermissionMapper sysPermissionMapper;

    public PermissionServiceImpl(PermissionMapper permissionMapper,
            RolePermissionMapper rolePermissionMapper,
            UserPermissionMapper userPermissionMapper,
            UserRoleMapper userRoleMapper,
            UserService userService,
            SysPermissionMapper sysPermissionMapper) {
        this.permissionMapper = permissionMapper;
        this.rolePermissionMapper = rolePermissionMapper;
        this.userPermissionMapper = userPermissionMapper;
        this.userRoleMapper = userRoleMapper;
        this.userService = userService;
        this.sysPermissionMapper = sysPermissionMapper;
    }

    @Override
    public Set<String> getPermissionsByRole(String role) {
        if (role == null || role.isBlank())
            return Collections.emptySet();

        List<SysPermission> list = sysPermissionMapper.selectList(
                new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getRole, role));
        return list.stream()
                .map(SysPermission::getPermission)
                .filter(p -> p != null && !p.isBlank())
                .collect(Collectors.toSet());
    }

    @Override
    public List<Map<String, Object>> getPermissionTree() {
        List<Permission> permissions = permissionMapper.selectList(new QueryWrapper<Permission>().orderByAsc("sort"));
        return buildPermissionTree(permissions, 0L);
    }

    @Override
    public List<Map<String, Object>> getUserMenus() {
        SysUser currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return new ArrayList<>();
        }

        Set<Long> permissionIds = new HashSet<>();

        // 如果是超级管理员，返回所有菜单权限
        if ("SUPER_ADMIN".equals(currentUser.getRole())) {
            List<Permission> allPermissions = permissionMapper.selectList(null);
            for (Permission p : allPermissions) {
                if ("menu".equals(p.getType())) {
                    permissionIds.add(p.getId());
                }
            }
        } else {
            // 获取用户的角色权限
            List<UserRole> userRoles = userRoleMapper.selectList(
                    new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, currentUser.getId()));
            Set<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toSet());

            for (Long roleId : roleIds) {
                List<RolePermission> rolePermissions = rolePermissionMapper.selectList(
                        new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, roleId));
                for (RolePermission rp : rolePermissions) {
                    permissionIds.add(rp.getPermissionId());
                }
            }

            // 获取用户自己的权限
            List<UserPermission> userPermissions = userPermissionMapper.selectList(
                    new LambdaQueryWrapper<UserPermission>().eq(UserPermission::getUserId, currentUser.getId()));
            for (UserPermission up : userPermissions) {
                permissionIds.add(up.getPermissionId());
            }
        }

        // 获取所有菜单权限
        List<Permission> allPermissions = permissionMapper.selectList(
                new LambdaQueryWrapper<Permission>().eq(Permission::getType, "menu"));
        List<Permission> userMenus = new ArrayList<>();
        for (Permission p : allPermissions) {
            if (permissionIds.contains(p.getId())) {
                userMenus.add(p);
            }
        }

        // 构建菜单树
        return buildMenuTree(userMenus);
    }

    private List<Map<String, Object>> buildMenuTree(List<Permission> menus) {
        List<Map<String, Object>> tree = new ArrayList<>();
        for (Permission menu : menus) {
            if (menu.getParentId() == null) {
                Map<String, Object> node = new HashMap<>();
                node.put("id", menu.getId());
                node.put("name", menu.getName());
                node.put("code", menu.getCode());
                node.put("path", menu.getPath());
                node.put("component", menu.getComponent());
                node.put("icon", menu.getIcon());
                node.put("sort", menu.getSort());

                List<Map<String, Object>> children = buildChildren(menu.getId(), menus);
                if (!children.isEmpty()) {
                    node.put("children", children);
                }

                tree.add(node);
            }
        }
        return tree;
    }

    private List<Map<String, Object>> buildChildren(Long parentId, List<Permission> allMenus) {
        List<Map<String, Object>> children = new ArrayList<>();
        for (Permission menu : allMenus) {
            if (parentId.equals(menu.getParentId())) {
                Map<String, Object> node = new HashMap<>();
                node.put("id", menu.getId());
                node.put("name", menu.getName());
                node.put("code", menu.getCode());
                node.put("path", menu.getPath());
                node.put("component", menu.getComponent());
                node.put("icon", menu.getIcon());
                node.put("sort", menu.getSort());

                List<Map<String, Object>> subChildren = buildChildren(menu.getId(), allMenus);
                if (!subChildren.isEmpty()) {
                    node.put("children", subChildren);
                }

                children.add(node);
            }
        }
        return children;
    }

    private List<Map<String, Object>> buildPermissionTree(List<Permission> permissions, Long parentId) {
        List<Map<String, Object>> tree = new ArrayList<>();

        for (Permission permission : permissions) {
            if ((permission.getParentId() == null && parentId == 0L)
                    || (permission.getParentId() != null && permission.getParentId().equals(parentId))) {
                Map<String, Object> node = new HashMap<>();
                node.put("id", permission.getId());
                node.put("name", permission.getName());
                node.put("code", permission.getCode());
                node.put("type", permission.getType());
                node.put("parentId", permission.getParentId());
                node.put("path", permission.getPath());
                node.put("component", permission.getComponent());
                node.put("icon", permission.getIcon());
                node.put("sort", permission.getSort());
                node.put("status", permission.getStatus());

                List<Map<String, Object>> children = buildPermissionTree(permissions, permission.getId());
                if (!children.isEmpty()) {
                    node.put("children", children);
                }

                tree.add(node);
            }
        }

        return tree;
    }

    @Override
    public boolean save(Permission permission) {
        return permissionMapper.insert(permission) > 0;
    }

    @Override
    public boolean update(Permission permission) {
        return permissionMapper.updateById(permission) > 0;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        deleteChildren(id);
        return permissionMapper.deleteById(id) > 0;
    }

    @Transactional
    public void deleteChildren(Long parentId) {
        List<Permission> children = permissionMapper
                .selectList(new LambdaQueryWrapper<Permission>().eq(Permission::getParentId, parentId));
        for (Permission child : children) {
            deleteChildren(child.getId());
            rolePermissionMapper.delete(
                    new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getPermissionId, child.getId()));
            permissionMapper.deleteById(child.getId());
        }
        rolePermissionMapper
                .delete(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getPermissionId, parentId));
    }
}