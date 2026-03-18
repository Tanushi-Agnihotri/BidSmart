package com.example.BidSmart.bid.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.example.BidSmart.bid.Bid;

public record BidResponse(
    UUID id,
    UUID auctionId,
    String auctionTitle,
    UUID bidderId,
    String bidderName,
    BigDecimal amount,
    OffsetDateTime createdAt
) {
    public static BidResponse from(Bid bid) {
        return new BidResponse(
            bid.getId(),
            bid.getAuction().getId(),
            bid.getAuction().getTitle(),
            bid.getBidder().getId(),
            bid.getBidder().getFullName(),
            bid.getAmount(),
            bid.getCreatedAt()
        );
    }
}
