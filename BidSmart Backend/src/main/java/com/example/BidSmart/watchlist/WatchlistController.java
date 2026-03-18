package com.example.BidSmart.watchlist;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.BidSmart.user.User;
import com.example.BidSmart.watchlist.dto.WatchlistResponse;
import com.example.BidSmart.watchlist.dto.WatchlistStatusResponse;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @PostMapping("/{auctionId}")
    public ResponseEntity<WatchlistStatusResponse> toggleWatchlist(
            @PathVariable UUID auctionId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(watchlistService.toggleWatchlist(auctionId, user));
    }

    @GetMapping
    public ResponseEntity<List<WatchlistResponse>> getMyWatchlist(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(watchlistService.getMyWatchlist(user.getId()));
    }

    @GetMapping("/check/{auctionId}")
    public ResponseEntity<WatchlistStatusResponse> checkWatchlist(
            @PathVariable UUID auctionId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(watchlistService.checkWatchlist(auctionId, user.getId()));
    }
}
