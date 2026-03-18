package com.example.BidSmart.bid;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.BidSmart.bid.dto.BidResponse;
import com.example.BidSmart.bid.dto.PlaceBidRequest;
import com.example.BidSmart.user.User;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bids")
public class BidController {

    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @PostMapping
    public ResponseEntity<BidResponse> placeBid(
            @Valid @RequestBody PlaceBidRequest request,
            Authentication authentication) {
        User bidder = (User) authentication.getPrincipal();
        BidResponse response = bidService.placeBid(request, bidder);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<List<BidResponse>> getBidsByAuction(@PathVariable UUID auctionId) {
        return ResponseEntity.ok(bidService.getBidsByAuction(auctionId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<BidResponse>> getMyBids(Authentication authentication) {
        User bidder = (User) authentication.getPrincipal();
        return ResponseEntity.ok(bidService.getMyBids(bidder.getId()));
    }
}
