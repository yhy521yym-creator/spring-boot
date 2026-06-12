package com.example.servicearea.dto;

public record LoginResponse(
        String token,
        UserProfile user
) {
}

