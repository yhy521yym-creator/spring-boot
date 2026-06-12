package com.example.servicearea.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 当前登录用户工具类（配合 JWT Filter 使用）
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static UserPrincipal getPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return null;
        }
        if (auth.getPrincipal() instanceof UserPrincipal principal) {
            return principal;
        }
        return null;
    }

    public static String currentRole() {
        UserPrincipal p = getPrincipal();
        return p != null ? p.getRole() : null;
    }

    public static boolean isSuperAdmin() {
        return "SUPER_ADMIN".equals(currentRole());
    }

    public static boolean isOperator() {
        return "OPERATOR".equals(currentRole());
    }

    public static boolean isPlatformAdmin() {
        return isSuperAdmin() || isOperator();
    }

    public static boolean isMerchant() {
        return "MERCHANT".equals(currentRole());
    }

    public static boolean isEndUser() {
        return "USER".equals(currentRole());
    }

    public static void deny(String message) {
        throw new AccessDeniedException(message);
    }
}
