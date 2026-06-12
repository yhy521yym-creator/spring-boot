package com.example.servicearea.controller;

import com.example.servicearea.common.ApiResponse;
import com.example.servicearea.dto.LoginRequest;
import com.example.servicearea.dto.LoginResponse;
import com.example.servicearea.dto.RegisterRequest;
import com.example.servicearea.dto.UserProfile;
import com.example.servicearea.entity.SysUser;
import com.example.servicearea.security.JwtService;
import com.example.servicearea.service.UserService;
import com.example.servicearea.support.MerchantAccessHelper;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MerchantAccessHelper merchantAccessHelper;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtService jwtService,
                          MerchantAccessHelper merchantAccessHelper) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.merchantAccessHelper = merchantAccessHelper;
    }

    /**
     * 登录接口：
     * - 校验用户名密码（BCrypt）
     * - 返回 JWT + 用户信息/角色
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        SysUser user = userService.findByUsername(req.username());
        if (user == null) {
            return ApiResponse.fail(4001, "用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            return ApiResponse.fail(4003, "账号已禁用");
        }
        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            return ApiResponse.fail(4001, "用户名或密码错误");
        }

        try {
            merchantAccessHelper.assertMerchantUserCanLogin(user);
        } catch (IllegalStateException e) {
            return ApiResponse.fail(4003, e.getMessage());
        }

        String token = jwtService.generateToken(user.getUsername(), user.getRole());
        UserProfile profile = new UserProfile(user.getId(), user.getUsername(), user.getPhone(), user.getRole(),
                user.getAvatar(), user.getMerchantId());
        return ApiResponse.ok(new LoginResponse(token, profile));
    }

    /**
     * 普通用户注册（答辩演示：注册后 role=USER）
     */
    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest req) {
        if (userService.existsByUsername(req.username())) {
            return ApiResponse.fail(400, "用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(req.username());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setEmail(req.email());
        user.setRole("USER");
        user.setStatus(1);
        userService.create(user);
        return ApiResponse.ok();
    }
}
