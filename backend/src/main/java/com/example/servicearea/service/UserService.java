package com.example.servicearea.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.servicearea.entity.SysUser;
import com.example.servicearea.mapper.SysUserMapper;
import com.example.servicearea.security.UserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final SysUserMapper userMapper;

    public UserService(SysUserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public SysUser findByUsername(String username) {
        if (username == null || username.isBlank())
            return null;
        return userMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
    }

    public SysUser findById(Long id) {
        if (id == null)
            return null;
        return userMapper.selectById(id);
    }

    public void updatePassword(Long userId, String bcryptPassword) {
        SysUser u = new SysUser();
        u.setId(userId);
        u.setPassword(bcryptPassword);
        userMapper.updateById(u);
    }

    public SysUser getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        if (auth.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
            return findById(principal.getId());
        }
        return null;
    }

    public IPage<SysUser> getPage(int pageNum, int pageSize, String username, String email, String role) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (username != null && !username.isBlank()) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (email != null && !email.isBlank()) {
            wrapper.like(SysUser::getEmail, email);
        }
        if (role != null && !role.isBlank()) {
            wrapper.eq(SysUser::getRole, role);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        return userMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    public List<SysUser> findMerchantUsersByMerchantId(Long merchantId) {
        if (merchantId == null) {
            return List.of();
        }
        return userMapper.selectList(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getRole, "MERCHANT")
                .eq(SysUser::getMerchantId, merchantId)
                .orderByAsc(SysUser::getId));
    }

    public Map<Long, List<SysUser>> mapMerchantUsers() {
        List<SysUser> merchants = userMapper.selectList(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getRole, "MERCHANT")
                .isNotNull(SysUser::getMerchantId));
        return merchants.stream().collect(Collectors.groupingBy(SysUser::getMerchantId));
    }

    public boolean existsByUsername(String username) {
        return userMapper.exists(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
    }

    public SysUser create(SysUser user) {
        userMapper.insert(user);
        return user;
    }

    public SysUser update(SysUser user) {
        userMapper.updateById(user);
        return user;
    }

    public void updateFields(Long id, String username, String email, String role, Integer status,
                             Long merchantId, boolean clearMerchantId) {
        LambdaUpdateWrapper<SysUser> uw = new LambdaUpdateWrapper<SysUser>().eq(SysUser::getId, id);
        if (username != null) {
            uw.set(SysUser::getUsername, username);
        }
        if (email != null) {
            uw.set(SysUser::getEmail, email);
        }
        if (role != null) {
            uw.set(SysUser::getRole, role);
        }
        if (status != null) {
            uw.set(SysUser::getStatus, status);
        }
        if (clearMerchantId) {
            uw.set(SysUser::getMerchantId, null);
        } else if (merchantId != null) {
            uw.set(SysUser::getMerchantId, merchantId);
        }
        userMapper.update(null, uw);
    }

    public void delete(Long id) {
        userMapper.deleteById(id);
    }
}