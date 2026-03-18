package com.example.BidSmart.bid;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, UUID> {

    List<Bid> findByAuctionIdOrderByAmountDesc(UUID auctionId);

    List<Bid> findByBidderIdOrderByCreatedAtDesc(UUID bidderId);

    int countByAuctionId(UUID auctionId);
}
