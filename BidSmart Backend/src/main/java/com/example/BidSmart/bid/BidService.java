package com.example.BidSmart.bid;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.BidSmart.auction.Auction;
import com.example.BidSmart.auction.AuctionRepository;
import com.example.BidSmart.auction.AuctionStatus;
import com.example.BidSmart.bid.dto.BidResponse;
import com.example.BidSmart.bid.dto.PlaceBidRequest;
import com.example.BidSmart.exception.ApiException;
import com.example.BidSmart.notification.NotificationService;
import com.example.BidSmart.notification.NotificationType;
import com.example.BidSmart.user.User;

@Service
public class BidService {

    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final NotificationService notificationService;

    public BidService(BidRepository bidRepository, AuctionRepository auctionRepository, NotificationService notificationService) {
        this.bidRepository = bidRepository;
        this.auctionRepository = auctionRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public BidResponse placeBid(PlaceBidRequest request, User bidder) {
        Auction auction = auctionRepository.findById(request.auctionId())
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Auction not found"));

        // Auction must be active or ending soon
        if (auction.getStatus() != AuctionStatus.ACTIVE && auction.getStatus() != AuctionStatus.ENDING_SOON) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "This auction is not accepting bids");
        }

        // Seller cannot bid on their own auction
        if (auction.getSeller().getId().equals(bidder.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You cannot bid on your own auction");
        }

        // Calculate minimum bid
        BigDecimal minimumBid;
        if (auction.getCurrentBid().compareTo(BigDecimal.ZERO) == 0) {
            minimumBid = auction.getBasePrice();
        } else {
            minimumBid = auction.getCurrentBid().add(auction.getBidIncrement());
        }

        // Validate bid amount
        if (request.amount().compareTo(minimumBid) < 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                "Bid must be at least $" + minimumBid.toPlainString());
        }

        // Create bid
        Bid bid = new Bid();
        bid.setAuction(auction);
        bid.setBidder(bidder);
        bid.setAmount(request.amount());

        Bid saved = bidRepository.save(bid);

        // Find previous highest bidder before updating
        List<Bid> previousBids = bidRepository.findByAuctionIdOrderByAmountDesc(request.auctionId());
        User previousHighestBidder = previousBids.isEmpty() ? null : previousBids.get(0).getBidder();

        // Update auction's current bid and total bids
        auction.setCurrentBid(request.amount());
        auction.setTotalBids(auction.getTotalBids() + 1);
        auctionRepository.save(auction);

        // Notify seller about new bid
        notificationService.createNotification(
            auction.getSeller(),
            NotificationType.BID,
            "New bid on \"" + auction.getTitle() + "\"",
            bidder.getFullName() + " placed a bid of $" + request.amount().toPlainString()
        );

        // Notify previous highest bidder that they've been outbid
        if (previousHighestBidder != null && !previousHighestBidder.getId().equals(bidder.getId())) {
            notificationService.createNotification(
                previousHighestBidder,
                NotificationType.BID,
                "You've been outbid on \"" + auction.getTitle() + "\"",
                "A new bid of $" + request.amount().toPlainString() + " has been placed. Place a higher bid to stay in the race!"
            );
        }

        return BidResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<BidResponse> getBidsByAuction(UUID auctionId) {
        // Verify auction exists
        if (!auctionRepository.existsById(auctionId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Auction not found");
        }

        return bidRepository.findByAuctionIdOrderByAmountDesc(auctionId)
            .stream()
            .map(BidResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<BidResponse> getMyBids(UUID bidderId) {
        return bidRepository.findByBidderIdOrderByCreatedAtDesc(bidderId)
            .stream()
            .map(BidResponse::from)
            .toList();
    }
}
