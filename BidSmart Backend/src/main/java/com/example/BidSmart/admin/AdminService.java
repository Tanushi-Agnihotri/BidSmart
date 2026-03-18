package com.example.BidSmart.admin;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.BidSmart.admin.dto.AdminUserResponse;
import com.example.BidSmart.admin.dto.DashboardStatsResponse;
import com.example.BidSmart.auction.AuctionRepository;
import com.example.BidSmart.auction.AuctionStatus;
import com.example.BidSmart.bid.BidRepository;
import com.example.BidSmart.exception.ApiException;
import com.example.BidSmart.user.User;
import com.example.BidSmart.user.UserRepository;
import com.example.BidSmart.user.UserRole;
import com.example.BidSmart.user.UserStatus;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;

    public AdminService(UserRepository userRepository, AuctionRepository auctionRepository, BidRepository bidRepository) {
        this.userRepository = userRepository;
        this.auctionRepository = auctionRepository;
        this.bidRepository = bidRepository;
    }

    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats() {
        long totalUsers = userRepository.count();
        long totalSellers = userRepository.countByRole(UserRole.SELLER);
        long totalBuyers = userRepository.countByRole(UserRole.BUYER);
        long totalAuctions = auctionRepository.count();
        long activeAuctions = auctionRepository.countByStatus(AuctionStatus.ACTIVE)
            + auctionRepository.countByStatus(AuctionStatus.ENDING_SOON);
        long closedAuctions = auctionRepository.countByStatus(AuctionStatus.CLOSED);
        long totalBids = bidRepository.count();
        var totalRevenue = auctionRepository.sumClosedAuctionRevenue();

        return new DashboardStatsResponse(
            totalUsers, totalSellers, totalBuyers,
            totalAuctions, activeAuctions, closedAuctions,
            totalBids, totalRevenue
        );
    }

    @Transactional(readOnly = true)
    public List<AdminUserResponse> getAllUsers() {
        return userRepository.findAll()
            .stream()
            .map(AdminUserResponse::from)
            .toList();
    }

    @Transactional
    public AdminUserResponse updateUserStatus(UUID userId, UserStatus newStatus) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getRole() == UserRole.ADMIN) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Cannot change admin status");
        }

        user.setStatus(newStatus);
        User saved = userRepository.save(user);
        return AdminUserResponse.from(saved);
    }
}
