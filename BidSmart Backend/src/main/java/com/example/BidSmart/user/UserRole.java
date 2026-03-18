package com.example.BidSmart.user;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum UserRole {
    BUYER,
    SELLER,
    ADMIN;

    @JsonCreator
    public static UserRole from(String value) {
        for (UserRole role : values()) {
            if (role.name().equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid role: " + value);
    }
}
