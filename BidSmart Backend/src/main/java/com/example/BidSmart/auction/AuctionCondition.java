package com.example.BidSmart.auction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AuctionCondition {
    NEW("New"),
    LIKE_NEW("Like New"),
    EXCELLENT("Excellent"),
    VERY_GOOD("Very Good"),
    GOOD("Good"),
    FAIR("Fair"),
    RESTORED("Restored");

    private final String displayName;

    AuctionCondition(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static AuctionCondition fromDisplayName(String value) {
        for (AuctionCondition c : values()) {
            if (c.displayName.equalsIgnoreCase(value)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Unknown condition: " + value);
    }
}
