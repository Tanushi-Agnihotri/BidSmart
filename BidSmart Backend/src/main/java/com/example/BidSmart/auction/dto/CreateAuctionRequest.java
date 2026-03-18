package com.example.BidSmart.auction.dto;

import java.math.BigDecimal;

import com.example.BidSmart.auction.AuctionCondition;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAuctionRequest(
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be at most 200 characters")
    String title,

    @NotBlank(message = "Category is required")
    @Size(max = 50, message = "Category must be at most 50 characters")
    String category,

    @Size(max = 5000, message = "Description must be at most 5000 characters")
    String description,

    @NotNull(message = "Condition is required")
    AuctionCondition condition,

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.01", message = "Base price must be greater than 0")
    BigDecimal basePrice,

    @NotNull(message = "Bid increment is required")
    @DecimalMin(value = "0.01", message = "Bid increment must be greater than 0")
    BigDecimal bidIncrement,

    @NotNull(message = "Duration in hours is required")
    @Min(value = 1, message = "Duration must be at least 1 hour")
    Integer durationHours
) {
}
