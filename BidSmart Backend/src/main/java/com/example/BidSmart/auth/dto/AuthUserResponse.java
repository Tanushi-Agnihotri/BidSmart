package com.example.BidSmart.auth.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.example.BidSmart.user.User;
import com.example.BidSmart.user.UserRole;
import com.example.BidSmart.user.UserStatus;

public record AuthUserResponse(
    UUID id,
    String fullName,
    String email,
    UserRole role,
    UserStatus status,
    OffsetDateTime createdAt
) {
    public static AuthUserResponse from(User user) {
        return new AuthUserResponse(
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getRole(),
            user.getStatus(),
            user.getCreatedAt()
        );
    }
}
