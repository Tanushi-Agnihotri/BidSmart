package com.example.BidSmart.auction.dto;

import java.math.BigDecimal;

import com.example.BidSmart.auction.AuctionCondition;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

public record UpdateAuctionRequest(
    @Size(max = 200, message = "Title must be at most 200 characters")
    String title,

    @Size(max = 50, message = "Category must be at most 50 characters")
    String category,

    @Size(max = 5000, message = "Description must be at most 5000 characters")
    String description,

    AuctionCondition condition,

    @DecimalMin(value = "0.01", message = "Base price must be greater than 0")
    BigDecimal basePrice,

    @DecimalMin(value = "0.01", message = "Bid increment must be greater than 0")
    BigDecimal bidIncrement
) {
}
