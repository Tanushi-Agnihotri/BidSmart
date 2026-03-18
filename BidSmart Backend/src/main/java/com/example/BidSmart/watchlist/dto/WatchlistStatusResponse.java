package com.example.BidSmart.watchlist.dto;

public record WatchlistStatusResponse(
    boolean inWatchlist,
    String message
) {
}
