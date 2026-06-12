package com.example.servicearea.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.servicearea.entity.Role;
import com.example.servicearea.entity.RolePermission;
import com.example.servicearea.mapper.RoleMapper;
import com.example.servicearea.mapper.RolePermissionMapper;
import com.example.servicearea.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 */
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;

    public RoleServiceImpl(RoleMapper roleMapper, RolePermissionMapper rolePermissionMapper) {
        this.roleMapper = roleMapper;
        this.rolePermissionMapper = rolePermissionMapper;
    }

    @Override
    public List<Role> listAll() {
        return roleMapper.selectList(null);
    }

    @Override
    public IPage<Role> queryPage(Page<Role> page, String name) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isBlank()) {
            wrapper.like(Role::getName, name);
        }
        return roleMapper.selectPage(page, wrapper);
    }

    @Override
    public boolean save(Role role) {
        return roleMapper.insert(role) > 0;
    }

    @Override
    public boolean update(Role role) {
        return roleMapper.updateById(role) > 0;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        // 删除角色-权限关联
        rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, id));
        // 删除角色
        return roleMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean assignPermissions(Long roleId, List<Long> permissionIds) {
        // 删除原有的权限关联
        rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, roleId));

        // 新增权限关联
        for (Long permissionId : permissionIds) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            rolePermissionMapper.insert(rolePermission);
        }
        return true;
    }

    @Override
    public List<Long> getRolePermissionIds(Long roleId) {
        List<RolePermission> rolePermissions = rolePermissionMapper
                .selectList(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, roleId));
        return rolePermissions.stream().map(RolePermission::getPermissionId).collect(Collectors.toList());
    }
}
