package com.example.BidSmart.auction;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionImageRepository extends JpaRepository<AuctionImage, UUID> {
    List<AuctionImage> findByAuctionIdOrderBySortOrder(UUID auctionId);
    int countByAuctionId(UUID auctionId);
    void deleteByAuctionId(UUID auctionId);
    void deleteByAuctionIdAndId(UUID auctionId, UUID imageId);
}
