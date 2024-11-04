package com.artztall.auction_service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BidDTO {
    private String auctionId;
    private String userId;
    private BigDecimal amount;
}
