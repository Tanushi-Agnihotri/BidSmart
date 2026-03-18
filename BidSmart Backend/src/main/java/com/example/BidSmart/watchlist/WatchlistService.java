package com.example.BidSmart.watchlist;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.BidSmart.auction.Auction;
import com.example.BidSmart.auction.AuctionRepository;
import com.example.BidSmart.exception.ApiException;
import com.example.BidSmart.user.User;
import com.example.BidSmart.watchlist.dto.WatchlistResponse;
import com.example.BidSmart.watchlist.dto.WatchlistStatusResponse;

@Service
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final AuctionRepository auctionRepository;

    public WatchlistService(WatchlistRepository watchlistRepository, AuctionRepository auctionRepository) {
        this.watchlistRepository = watchlistRepository;
        this.auctionRepository = auctionRepository;
    }

    @Transactional
    public WatchlistStatusResponse toggleWatchlist(UUID auctionId, User user) {
        Auction auction = auctionRepository.findById(auctionId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Auction not found"));

        Optional<WatchlistItem> existing = watchlistRepository.findByUserIdAndAuctionId(user.getId(), auctionId);

        if (existing.isPresent()) {
            watchlistRepository.delete(existing.get());
            auction.setWatchlistCount(Math.max(0, auction.getWatchlistCount() - 1));
            auctionRepository.save(auction);
            return new WatchlistStatusResponse(false, "Removed from watchlist");
        } else {
            WatchlistItem item = new WatchlistItem();
            item.setUser(user);
            item.setAuction(auction);
            watchlistRepository.save(item);
            auction.setWatchlistCount(auction.getWatchlistCount() + 1);
            auctionRepository.save(auction);
            return new WatchlistStatusResponse(true, "Added to watchlist");
        }
    }

    @Transactional(readOnly = true)
    public List<WatchlistResponse> getMyWatchlist(UUID userId) {
        return watchlistRepository.findByUserIdOrderByAddedAtDesc(userId)
            .stream()
            .map(WatchlistResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public WatchlistStatusResponse checkWatchlist(UUID auctionId, UUID userId) {
        boolean exists = watchlistRepository.existsByUserIdAndAuctionId(userId, auctionId);
        return new WatchlistStatusResponse(exists, exists ? "In watchlist" : "Not in watchlist");
    }
}
