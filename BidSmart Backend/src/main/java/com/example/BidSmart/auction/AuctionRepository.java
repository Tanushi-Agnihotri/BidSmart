package com.example.BidSmart.auction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface AuctionRepository extends JpaRepository<Auction, UUID>, JpaSpecificationExecutor<Auction> {

    List<Auction> findBySellerIdOrderByCreatedAtDesc(UUID sellerId);

    List<Auction> findByStatusOrderByEndTimeAsc(AuctionStatus status);

    List<Auction> findByStatusInOrderByEndTimeAsc(List<AuctionStatus> statuses);

    List<Auction> findByCategoryAndStatusInOrderByEndTimeAsc(String category, List<AuctionStatus> statuses);

    long countByStatus(AuctionStatus status);

    @Query("SELECT COALESCE(SUM(a.currentBid), 0) FROM Auction a WHERE a.status = 'CLOSED' AND a.currentBid > 0")
    BigDecimal sumClosedAuctionRevenue();

    // For scheduler: find auctions that need status updates
    List<Auction> findByStatusAndEndTimeBefore(AuctionStatus status, OffsetDateTime time);

    List<Auction> findByStatusAndStartTimeBefore(AuctionStatus status, OffsetDateTime time);

    List<Auction> findByStatusAndEndTimeBetween(AuctionStatus status, OffsetDateTime from, OffsetDateTime to);
}
