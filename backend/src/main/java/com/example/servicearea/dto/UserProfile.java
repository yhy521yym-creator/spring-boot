package com.example.servicearea.dto;

public record UserProfile(
        Long id,
        String username,
        String phone,
        String role,
        String avatar,
        Long merchantId
) {
}

