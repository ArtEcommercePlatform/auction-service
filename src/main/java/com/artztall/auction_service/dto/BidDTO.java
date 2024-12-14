package com.artztall.auction_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
public class BidDTO {
    private String auctionId;
    private String userId;
    private double amount;
    private LocalDateTime bidTime;
}
