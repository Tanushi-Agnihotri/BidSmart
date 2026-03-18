package com.example.BidSmart.auction;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.BidSmart.auction.dto.AuctionResponse;
import com.example.BidSmart.auction.dto.CreateAuctionRequest;
import com.example.BidSmart.auction.dto.UpdateAuctionRequest;
import com.example.BidSmart.exception.ApiException;
import com.example.BidSmart.user.User;
import com.example.BidSmart.user.UserRole;

@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;

    public AuctionService(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    @Transactional(readOnly = true)
    public List<AuctionResponse> getAllAuctions(String category, String status) {
        List<Auction> auctions;

        if (category != null && !category.isBlank() && status != null && !status.isBlank()) {
            List<AuctionStatus> statuses = parseStatuses(status);
            auctions = auctionRepository.findByCategoryAndStatusInOrderByEndTimeAsc(category, statuses);
        } else if (status != null && !status.isBlank()) {
            List<AuctionStatus> statuses = parseStatuses(status);
            auctions = auctionRepository.findByStatusInOrderByEndTimeAsc(statuses);
        } else {
            auctions = auctionRepository.findByStatusInOrderByEndTimeAsc(
                List.of(AuctionStatus.ACTIVE, AuctionStatus.ENDING_SOON, AuctionStatus.UPCOMING)
            );
        }

        return auctions.stream().map(AuctionResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public AuctionResponse getAuctionById(UUID id) {
        Auction auction = findAuctionOrThrow(id);
        return AuctionResponse.from(auction);
    }

    @Transactional(readOnly = true)
    public List<AuctionResponse> getAuctionsBySeller(UUID sellerId) {
        return auctionRepository.findBySellerIdOrderByCreatedAtDesc(sellerId)
            .stream()
            .map(AuctionResponse::from)
            .toList();
    }

    @Transactional
    public AuctionResponse createAuction(CreateAuctionRequest request, User seller) {
        if (seller.getRole() != UserRole.SELLER) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Only sellers can create auctions");
        }

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime endTime = now.plusHours(request.durationHours());

        Auction auction = new Auction();
        auction.setTitle(request.title().trim());
        auction.setCategory(request.category().trim());
        auction.setDescription(request.description() != null ? request.description().trim() : null);
        auction.setItemCondition(request.condition());
        auction.setBasePrice(request.basePrice());
        auction.setBidIncrement(request.bidIncrement());
        auction.setStartTime(now);
        auction.setEndTime(endTime);
        auction.setStatus(AuctionStatus.ACTIVE);
        auction.setSeller(seller);

        Auction saved = auctionRepository.save(auction);
        return AuctionResponse.from(saved);
    }

    @Transactional
    public AuctionResponse updateAuction(UUID auctionId, UpdateAuctionRequest request, User seller) {
        Auction auction = findAuctionOrThrow(auctionId);

        if (!auction.getSeller().getId().equals(seller.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can only edit your own auctions");
        }

        if (auction.getStatus() == AuctionStatus.CLOSED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot edit a closed auction");
        }

        if (auction.getTotalBids() > 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot edit an auction that already has bids");
        }

        if (request.title() != null) auction.setTitle(request.title().trim());
        if (request.category() != null) auction.setCategory(request.category().trim());
        if (request.description() != null) auction.setDescription(request.description().trim());
        if (request.condition() != null) auction.setItemCondition(request.condition());
        if (request.basePrice() != null) auction.setBasePrice(request.basePrice());
        if (request.bidIncrement() != null) auction.setBidIncrement(request.bidIncrement());

        Auction saved = auctionRepository.save(auction);
        return AuctionResponse.from(saved);
    }

    @Transactional
    public void deleteAuction(UUID auctionId, User seller) {
        Auction auction = findAuctionOrThrow(auctionId);

        if (!auction.getSeller().getId().equals(seller.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can only delete your own auctions");
        }

        if (auction.getTotalBids() > 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot delete an auction that has bids");
        }

        auctionRepository.delete(auction);
    }

    private Auction findAuctionOrThrow(UUID id) {
        return auctionRepository.findById(id)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Auction not found"));
    }

    private List<AuctionStatus> parseStatuses(String status) {
        try {
            return List.of(AuctionStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid status: " + status);
        }
    }
}
