package com.example.BidSmart.admin.dto;

import com.example.BidSmart.user.UserStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(
    @NotNull(message = "Status is required")
    UserStatus status
) {
}
