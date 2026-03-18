package com.example.BidSmart.auction;

import java.time.OffsetDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.BidSmart.bid.BidRepository;
import com.example.BidSmart.notification.NotificationService;
import com.example.BidSmart.notification.NotificationType;

@Component
public class AuctionScheduler {

    private static final Logger log = LoggerFactory.getLogger(AuctionScheduler.class);

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final NotificationService notificationService;

    public AuctionScheduler(AuctionRepository auctionRepository, BidRepository bidRepository, NotificationService notificationService) {
        this.auctionRepository = auctionRepository;
        this.bidRepository = bidRepository;
        this.notificationService = notificationService;
    }

    /**
     * Runs every minute to update auction statuses.
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateAuctionStatuses() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime oneHourFromNow = now.plusHours(1);

        // UPCOMING -> ACTIVE (start time has passed)
        List<Auction> toActivate = auctionRepository.findByStatusAndStartTimeBefore(AuctionStatus.UPCOMING, now);
        for (Auction auction : toActivate) {
            auction.setStatus(AuctionStatus.ACTIVE);
            auctionRepository.save(auction);
            log.info("Auction activated: {}", auction.getTitle());
        }

        // ACTIVE -> ENDING_SOON (less than 1 hour remaining)
        List<Auction> toEndingSoon = auctionRepository.findByStatusAndEndTimeBetween(AuctionStatus.ACTIVE, now, oneHourFromNow);
        for (Auction auction : toEndingSoon) {
            auction.setStatus(AuctionStatus.ENDING_SOON);
            auctionRepository.save(auction);
            log.info("Auction ending soon: {}", auction.getTitle());
        }

        // ACTIVE/ENDING_SOON -> CLOSED (end time has passed)
        List<Auction> toCloseActive = auctionRepository.findByStatusAndEndTimeBefore(AuctionStatus.ACTIVE, now);
        List<Auction> toCloseEndingSoon = auctionRepository.findByStatusAndEndTimeBefore(AuctionStatus.ENDING_SOON, now);

        closeAuctions(toCloseActive);
        closeAuctions(toCloseEndingSoon);
    }

    private void closeAuctions(List<Auction> auctions) {
        for (Auction auction : auctions) {
            auction.setStatus(AuctionStatus.CLOSED);
            auctionRepository.save(auction);
            log.info("Auction closed: {}", auction.getTitle());

            // Notify seller
            notificationService.createNotification(
                auction.getSeller(),
                NotificationType.AUCTION,
                "Auction ended: \"" + auction.getTitle() + "\"",
                auction.getTotalBids() > 0
                    ? "Your auction has ended with a winning bid of $" + auction.getCurrentBid().toPlainString()
                    : "Your auction has ended with no bids."
            );

            // Notify winner (highest bidder)
            if (auction.getTotalBids() > 0) {
                var topBids = bidRepository.findByAuctionIdOrderByAmountDesc(auction.getId());
                if (!topBids.isEmpty()) {
                    notificationService.createNotification(
                        topBids.get(0).getBidder(),
                        NotificationType.AUCTION,
                        "You won: \"" + auction.getTitle() + "\"",
                        "Congratulations! You won the auction with a bid of $" + topBids.get(0).getAmount().toPlainString()
                    );
                }
            }
        }
    }
}
