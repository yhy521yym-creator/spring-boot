package com.example.servicearea.security;

import com.example.servicearea.entity.SysUser;
import com.example.servicearea.service.UserService;
import org.springframework.stereotype.Component;

/**
 * 按角色强制数据范围（不信任前端传入的 userId / merchantId）
 */
@Component
public class DataScopeHelper {

    private final UserService userService;

    public DataScopeHelper(UserService userService) {
        this.userService = userService;
    }

    public SysUser requireCurrentUser() {
        SysUser user = userService.getCurrentUser();
        if (user == null) {
            SecurityUtils.deny("未登录或登录已过期");
        }
        return user;
    }

    /** 查询订单/预约时：返回应强制使用的 userId（普通用户） */
    public Long scopedUserId(Long requestedUserId) {
        SysUser current = requireCurrentUser();
        if (SecurityUtils.isEndUser()) {
            return current.getId();
        }
        if (SecurityUtils.isMerchant()) {
            return null;
        }
        if (SecurityUtils.isPlatformAdmin()) {
            return requestedUserId;
        }
        return requestedUserId;
    }

    /** 查询订单/预约/商品时：返回应强制使用的 merchantId（商户） */
    public Long scopedMerchantId(Long requestedMerchantId) {
        SysUser current = requireCurrentUser();
        if (SecurityUtils.isMerchant()) {
            if (current.getMerchantId() == null) {
                SecurityUtils.deny("商户账号未绑定店铺");
            }
            return current.getMerchantId();
        }
        if (SecurityUtils.isEndUser()) {
            return null;
        }
        if (SecurityUtils.isPlatformAdmin()) {
            return requestedMerchantId;
        }
        return requestedMerchantId;
    }

    public boolean canManageOrders() {
        return SecurityUtils.isPlatformAdmin() || SecurityUtils.isMerchant();
    }

    public boolean canManageReservations() {
        return SecurityUtils.isPlatformAdmin() || SecurityUtils.isMerchant();
    }
}
