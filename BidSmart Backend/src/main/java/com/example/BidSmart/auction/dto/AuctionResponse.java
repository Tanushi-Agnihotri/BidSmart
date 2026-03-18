package com.example.BidSmart.auction.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.example.BidSmart.auction.Auction;
import com.example.BidSmart.auction.AuctionCondition;
import com.example.BidSmart.auction.AuctionStatus;

public record AuctionResponse(
    UUID id,
    String title,
    String category,
    String description,
    AuctionCondition condition,
    BigDecimal basePrice,
    BigDecimal currentBid,
    BigDecimal bidIncrement,
    int totalBids,
    int watchlistCount,
    OffsetDateTime startTime,
    OffsetDateTime endTime,
    AuctionStatus status,
    UUID sellerId,
    String sellerName,
    OffsetDateTime createdAt
) {
    public static AuctionResponse from(Auction auction) {
        return new AuctionResponse(
            auction.getId(),
            auction.getTitle(),
            auction.getCategory(),
            auction.getDescription(),
            auction.getItemCondition(),
            auction.getBasePrice(),
            auction.getCurrentBid(),
            auction.getBidIncrement(),
            auction.getTotalBids(),
            auction.getWatchlistCount(),
            auction.getStartTime(),
            auction.getEndTime(),
            auction.getStatus(),
            auction.getSeller().getId(),
            auction.getSeller().getFullName(),
            auction.getCreatedAt()
        );
    }
}
