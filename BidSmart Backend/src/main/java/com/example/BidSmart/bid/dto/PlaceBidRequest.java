package com.example.BidSmart.bid.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record PlaceBidRequest(
    @NotNull(message = "Auction ID is required")
    UUID auctionId,

    @NotNull(message = "Bid amount is required")
    @DecimalMin(value = "0.01", message = "Bid amount must be greater than 0")
    BigDecimal amount
) {
}
