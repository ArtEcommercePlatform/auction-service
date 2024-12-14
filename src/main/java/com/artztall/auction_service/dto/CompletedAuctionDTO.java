package com.artztall.auction_service.dto;

import com.artztall.auction_service.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompletedAuctionDTO {
    private String id;
    private String title;
    private String description;
    private String paintingUrl;
    private double finalPrice;
    private String winnerId;
    private LocalDateTime endTime;
    private PaymentStatus paymentStatus;
}