package com.example.BidSmart.admin.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.example.BidSmart.user.User;
import com.example.BidSmart.user.UserRole;
import com.example.BidSmart.user.UserStatus;

public record AdminUserResponse(
    UUID id,
    String fullName,
    String email,
    UserRole role,
    UserStatus status,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
    public static AdminUserResponse from(User user) {
        return new AdminUserResponse(
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getRole(),
            user.getStatus(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
