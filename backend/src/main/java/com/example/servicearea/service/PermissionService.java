package com.example.servicearea.service;

import com.example.servicearea.entity.Permission;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PermissionService {

    Set<String> getPermissionsByRole(String role);

    List<Map<String, Object>> getPermissionTree();

    List<Map<String, Object>> getUserMenus();

    boolean save(Permission permission);

    boolean update(Permission permission);

    boolean delete(Long id);
}