package com.example.BidSmart.user;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.BidSmart.auction.ImageStorageService;
import com.example.BidSmart.exception.ApiException;
import com.example.BidSmart.user.dto.BecomeSellerRequest;

@Service
public class SellerProfileService {

    private final SellerProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ImageStorageService imageStorageService;

    public SellerProfileService(SellerProfileRepository profileRepository, UserRepository userRepository, ImageStorageService imageStorageService) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.imageStorageService = imageStorageService;
    }

    @Transactional
    public SellerProfile createProfile(User user, BecomeSellerRequest request, MultipartFile idDocument) {
        if (profileRepository.findByUserId(user.getId()).isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Seller profile already exists");
        }

        String idUrl = imageStorageService.store(idDocument);

        SellerProfile profile = new SellerProfile();
        profile.setUser(user);
        profile.setStoreName(request.storeName());
        profile.setBusinessCategory(request.businessCategory());
        profile.setDescription(request.description());
        profile.setLegalName(request.legalName());
        profile.setAccountHolderName(request.accountHolderName());
        profile.setBankName(request.bankName());
        profile.setRoutingNumber(request.routingNumber());
        profile.setAccountNumber(request.accountNumber());
        profile.setIdDocumentUrl(idUrl);

        // Auto approve
        profile.setStatus("APPROVED");

        user.setRole(UserRole.SELLER);
        userRepository.save(user);

        return profileRepository.save(profile);
    }
    
    @Transactional(readOnly = true)
    public SellerProfile getProfile(User user) {
        return profileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Seller profile not found"));
    }
}
