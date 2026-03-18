package com.example.BidSmart.admin.dto;

import java.math.BigDecimal;

public record DashboardStatsResponse(
    long totalUsers,
    long totalSellers,
    long totalBuyers,
    long totalAuctions,
    long activeAuctions,
    long closedAuctions,
    long totalBids,
    BigDecimal totalRevenue
) {
}
