package com.example.servicearea.controller;

import com.example.servicearea.common.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用于联调验证 403（权限不足）行为的示例接口。
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/ping")
    @PreAuthorize("hasAuthority('*') or hasRole('SUPER_ADMIN')")
    public ApiResponse<String> ping() {
        return ApiResponse.ok("pong");
    }
}

