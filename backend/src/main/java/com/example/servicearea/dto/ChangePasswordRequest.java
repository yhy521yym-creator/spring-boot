package com.example.servicearea.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "oldPassword 不能为空") String oldPassword,
        @NotBlank(message = "newPassword 不能为空")
        @Size(min = 6, message = "newPassword 至少 6 位") String newPassword
) {
}

