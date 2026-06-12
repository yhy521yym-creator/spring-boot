package com.example.servicearea.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.servicearea.entity.Role;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService {

    /**
     * 获取所有角色（用于下拉框）
     *
     * @return 角色列表
     */
    List<Role> listAll();

    /**
     * 分页查询角色
     *
     * @param page 分页参数
     * @param name 角色名称（模糊搜索）
     * @return 分页结果
     */
    IPage<Role> queryPage(Page<Role> page, String name);

    /**
     * 新增角色
     *
     * @param role 角色信息
     * @return 是否成功
     */
    boolean save(Role role);

    /**
     * 修改角色
     *
     * @param role 角色信息
     * @return 是否成功
     */
    boolean update(Role role);

    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return 是否成功
     */
    boolean delete(Long id);

    /**
     * 分配权限
     *
     * @param roleId        角色ID
     * @param permissionIds 权限ID列表
     * @return 是否成功
     */
    boolean assignPermissions(Long roleId, List<Long> permissionIds);

    /**
     * 查询角色的权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> getRolePermissionIds(Long roleId);
}
