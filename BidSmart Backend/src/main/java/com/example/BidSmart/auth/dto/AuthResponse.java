package com.example.BidSmart.auth.dto;

public record AuthResponse(
    String token,
    String tokenType,
    AuthUserResponse user
) {
}
