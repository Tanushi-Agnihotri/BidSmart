package com.example.BidSmart.user.dto;

import jakarta.validation.constraints.NotBlank;

public record BecomeSellerRequest(
    @NotBlank(message = "Store Name is required") String storeName,
    @NotBlank(message = "Business Category is required") String businessCategory,
    @NotBlank(message = "Description is required") String description,
    @NotBlank(message = "Legal Name is required") String legalName,
    @NotBlank(message = "Account Holder Name is required") String accountHolderName,
    @NotBlank(message = "Bank Name is required") String bankName,
    @NotBlank(message = "Routing Number is required") String routingNumber,
    @NotBlank(message = "Account Number is required") String accountNumber
) {}
