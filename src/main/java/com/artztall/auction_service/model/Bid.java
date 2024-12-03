package com.artztall.auction_service.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Bid {
    private String id;
    private String auctionId;
    private String userId;
    private double amount;
    private LocalDateTime bidTime;
}