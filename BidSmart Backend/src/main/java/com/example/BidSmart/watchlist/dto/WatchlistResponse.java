package com.example.BidSmart.watchlist.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.example.BidSmart.auction.dto.AuctionResponse;
import com.example.BidSmart.watchlist.WatchlistItem;

public record WatchlistResponse(
    UUID id,
    AuctionResponse auction,
    OffsetDateTime addedAt
) {
    public static WatchlistResponse from(WatchlistItem item) {
        return new WatchlistResponse(
            item.getId(),
            AuctionResponse.from(item.getAuction()),
            item.getAddedAt()
        );
    }
}
