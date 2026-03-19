package com.example.BidSmart.watchlist;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchlistRepository extends JpaRepository<WatchlistItem, UUID> {

    List<WatchlistItem> findByUserIdOrderByAddedAtDesc(UUID userId);

    Optional<WatchlistItem> findByUserIdAndAuctionId(UUID userId, UUID auctionId);

    boolean existsByUserIdAndAuctionId(UUID userId, UUID auctionId);

    int countByAuctionId(UUID auctionId);

    void deleteByAuctionId(UUID auctionId);
}
