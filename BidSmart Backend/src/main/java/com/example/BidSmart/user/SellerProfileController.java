package com.example.BidSmart.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.BidSmart.user.dto.BecomeSellerRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users/me/seller-profile")
public class SellerProfileController {

    private final SellerProfileService profileService;

    public SellerProfileController(SellerProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping
    public ResponseEntity<Void> becomeSeller(
            @Valid @RequestPart("data") BecomeSellerRequest request,
            @RequestPart("idDocument") MultipartFile idDocument,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        profileService.createProfile(user, request, idDocument);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    @GetMapping
    public ResponseEntity<SellerProfile> getMyProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(profileService.getProfile(user));
    }
}
