package com.example.servicearea.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "username 不能为空") String username,
        @NotBlank(message = "password 不能为空") @Size(min = 6, message = "password 至少 6 位") String password,
        @Email(message = "邮箱格式不正确") String email
) {
}
