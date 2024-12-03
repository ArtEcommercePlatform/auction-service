package com.artztall.auction_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "auctions")
public class Auction {
    @Id
    private String id;
    private String title;
    private String description;
    private String paintingUrl;
    private String artistId;
    private double startingPrice;
    private double currentPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AuctionStatus auctionStatus;
    private List<Bid> bids;
    private String winnerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Auction() {
        this.bids = new ArrayList<>();
    }

    public boolean hasBids() {
        return bids != null && !bids.isEmpty();
    }

    public Bid getHighestBid() {
        if (!hasBids()) {
            return null;
        }
        return bids.get(bids.size() - 1);
    }

    public void addBid(Bid bid) {
        if (bids == null) {
            bids = new ArrayList<>();
        }
        bids.add(bid);
    }

    public int getBidsCount() {
        return bids != null ? bids.size() : 0;
    }
}

