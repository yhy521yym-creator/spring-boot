package com.example.servicearea.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.servicearea.common.ApiResponse;
import com.example.servicearea.dto.ChangePasswordRequest;
import com.example.servicearea.dto.UserProfile;
import com.example.servicearea.entity.SysUser;
import com.example.servicearea.security.JwtService;
import com.example.servicearea.security.UserPrincipal;
import com.example.servicearea.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.Data;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserController(UserService userService, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @GetMapping("/me")
    public ApiResponse<UserProfile> me(HttpServletRequest request, @AuthenticationPrincipal UserPrincipal principal) {
        String username = null;
        
        if (principal != null) {
            username = principal.getUsername();
        } else {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7).trim();
                try {
                    Claims claims = jwtService.parseClaims(token);
                    username = claims.getSubject();
                } catch (Exception e) {
                    return ApiResponse.fail(401, "未登录或登录已过期");
                }
            }
        }

        if (username == null) {
            return ApiResponse.fail(401, "未登录或登录已过期");
        }

        SysUser user = userService.findByUsername(username);
        if (user == null) {
            return ApiResponse.fail(404, "用户不存在");
        }
        return ApiResponse.ok(new UserProfile(user.getId(), user.getUsername(), user.getPhone(), user.getRole(),
                user.getAvatar(), user.getMerchantId()));
    }

    @PutMapping("/me")
    public ApiResponse<UserProfile> updateMe(HttpServletRequest request,
                                             @AuthenticationPrincipal UserPrincipal principal,
                                             @Valid @RequestBody UpdateMyProfileRequest req) {
        String username = null;
        
        if (principal != null) {
            username = principal.getUsername();
        } else {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7).trim();
                try {
                    Claims claims = jwtService.parseClaims(token);
                    username = claims.getSubject();
                } catch (Exception e) {
                    return ApiResponse.fail(401, "未登录或登录已过期");
                }
            }
        }

        if (username == null) {
            return ApiResponse.fail(401, "未登录或登录已过期");
        }

        SysUser user = userService.findByUsername(username);
        if (user == null) {
            return ApiResponse.fail(404, "用户不存在");
        }

        if (req.phone != null) {
            user.setPhone(req.phone);
        }
        if (req.avatar != null) {
            user.setAvatar(req.avatar);
        }

        SysUser updated = userService.update(user);
        return ApiResponse.ok(new UserProfile(updated.getId(), updated.getUsername(), updated.getPhone(), updated.getRole(),
                updated.getAvatar(), updated.getMerchantId()));
    }

    @Data
    public static class UpdateMyProfileRequest {
        private String phone;
        private String avatar;
    }

    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@AuthenticationPrincipal UserPrincipal principal,
                                           @Valid @RequestBody ChangePasswordRequest req) {
        if (principal == null) {
            return ApiResponse.fail(401, "未登录或登录已过期");
        }

        SysUser user = userService.findById(principal.getId());
        if (user == null) {
            return ApiResponse.fail(404, "用户不存在");
        }

        if (!passwordEncoder.matches(req.oldPassword(), user.getPassword())) {
            return ApiResponse.fail(4002, "旧密码不正确");
        }

        String bcrypt = passwordEncoder.encode(req.newPassword());
        userService.updatePassword(user.getId(), bcrypt);
        return ApiResponse.ok();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('*') or hasAuthority('user:manage')")
    public ApiResponse<IPage<SysUser>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role) {
        return ApiResponse.ok(userService.getPage(pageNum, pageSize, username, email, role));
    }

    @GetMapping("/{id}")
    public ApiResponse<SysUser> getById(@PathVariable Long id,
                                        @AuthenticationPrincipal UserPrincipal principal) {
        if (principal != null && !principal.getId().equals(id)) {
            boolean isAdmin = principal.getAuthorities().stream()
                    .anyMatch(a -> "*".equals(a.getAuthority()) || "user:manage".equals(a.getAuthority()));
            if (!isAdmin) {
                return ApiResponse.fail(403, "无权限查看其他用户信息");
            }
        }
        SysUser user = userService.findById(id);
        if (user == null) {
            return ApiResponse.fail(404, "用户不存在");
        }
        user.setPassword(null);
        return ApiResponse.ok(user);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('*') or hasAuthority('user:manage')")
    public ApiResponse<SysUser> create(@Valid @RequestBody CreateUserRequest req) {
        if (userService.existsByUsername(req.username)) {
            return ApiResponse.fail(400, "用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(req.username);
        user.setPassword(passwordEncoder.encode(req.password));
        user.setEmail(req.email);
        user.setRole(req.role);
        user.setStatus(req.status != null ? req.status : 1);
        if ("MERCHANT".equals(req.role)) {
            if (req.merchantId == null) {
                return ApiResponse.fail(400, "商户角色必须选择所属店铺");
            }
            user.setMerchantId(req.merchantId);
        }

        SysUser created = userService.create(user);
        return ApiResponse.ok(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('*') or hasAuthority('user:manage')")
    public ApiResponse<SysUser> update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest req) {
        SysUser user = userService.findById(id);
        if (user == null) {
            return ApiResponse.fail(404, "用户不存在");
        }

        if (req.username != null && !req.username.equals(user.getUsername()) && userService.existsByUsername(req.username)) {
            return ApiResponse.fail(400, "用户名已存在");
        }

        String effectiveRole = req.role != null ? req.role : user.getRole();
        Long merchantId = null;
        boolean clearMerchantId = false;
        if ("MERCHANT".equals(effectiveRole)) {
            merchantId = req.merchantId != null ? req.merchantId : user.getMerchantId();
            if (merchantId == null) {
                return ApiResponse.fail(400, "商户角色必须绑定所属店铺");
            }
        } else if (req.role != null) {
            clearMerchantId = true;
        }

        userService.updateFields(id, req.username, req.email, req.role, req.status, merchantId, clearMerchantId);
        SysUser updated = userService.findById(id);
        if (updated != null) {
            updated.setPassword(null);
        }
        return ApiResponse.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('*') or hasAuthority('user:manage')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        SysUser user = userService.findById(id);
        if (user == null) {
            return ApiResponse.fail(404, "用户不存在");
        }

        if ("SUPER_ADMIN".equals(user.getRole())) {
            return ApiResponse.fail(400, "不能删除超级管理员");
        }

        userService.delete(id);
        return ApiResponse.ok();
    }

    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasAuthority('*') or hasAuthority('user:manage')")
    public ApiResponse<Void> resetPassword(@PathVariable Long id, @Valid @RequestBody ResetPasswordRequest req) {
        SysUser user = userService.findById(id);
        if (user == null) {
            return ApiResponse.fail(404, "用户不存在");
        }

        String bcrypt = passwordEncoder.encode(req.newPassword);
        userService.updatePassword(id, bcrypt);
        return ApiResponse.ok();
    }

    @Data
    public static class CreateUserRequest {
        @jakarta.validation.constraints.NotBlank(message = "用户名不能为空")
        private String username;

        @jakarta.validation.constraints.NotBlank(message = "密码不能为空")
        private String password;

        @Email(message = "邮箱格式不正确")
        private String email;

        @jakarta.validation.constraints.NotBlank(message = "角色不能为空")
        private String role;

        private Integer status;

        /** 商户角色必填 */
        private Long merchantId;
    }

    @Data
    public static class UpdateUserRequest {
        private String username;

        @Email(message = "邮箱格式不正确")
        private String email;

        private String role;

        private Integer status;

        private Long merchantId;
    }

    @Data
    public static class ResetPasswordRequest {
        @jakarta.validation.constraints.NotBlank(message = "新密码不能为空")
        private String newPassword;
    }
}