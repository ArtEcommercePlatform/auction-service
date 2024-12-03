package com.artztall.auction_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AuctionCreateDTO {

    private String title;

    private String description;


    private String paintingUrl;


    private String artistId;



    private double startingPrice;


    private LocalDateTime startTime;


    private LocalDateTime endTime;
}
