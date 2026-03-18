package com.example.BidSmart.auction;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.BidSmart.auction.dto.AuctionResponse;
import com.example.BidSmart.auction.dto.CreateAuctionRequest;
import com.example.BidSmart.auction.dto.UpdateAuctionRequest;
import com.example.BidSmart.user.User;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @GetMapping
    public ResponseEntity<List<AuctionResponse>> getAllAuctions(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(auctionService.getAllAuctions(category, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuctionResponse> getAuctionById(@PathVariable UUID id) {
        return ResponseEntity.ok(auctionService.getAuctionById(id));
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<AuctionResponse>> getAuctionsBySeller(@PathVariable UUID sellerId) {
        return ResponseEntity.ok(auctionService.getAuctionsBySeller(sellerId));
    }

    @PostMapping
    public ResponseEntity<AuctionResponse> createAuction(
            @Valid @RequestBody CreateAuctionRequest request,
            Authentication authentication) {
        User seller = (User) authentication.getPrincipal();
        AuctionResponse response = auctionService.createAuction(request, seller);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuctionResponse> updateAuction(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAuctionRequest request,
            Authentication authentication) {
        User seller = (User) authentication.getPrincipal();
        return ResponseEntity.ok(auctionService.updateAuction(id, request, seller));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuction(
            @PathVariable UUID id,
            Authentication authentication) {
        User seller = (User) authentication.getPrincipal();
        auctionService.deleteAuction(id, seller);
        return ResponseEntity.noContent().build();
    }
}
