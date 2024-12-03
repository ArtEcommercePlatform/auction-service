package com.artztall.auction_service.shedular;

import com.artztall.auction_service.service.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class AuctionScheduler {

    private final AuctionService auctionService;

    @Autowired
    public AuctionScheduler(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @Scheduled(fixedRate = 60000) // Runs every minute
    public void checkAndCloseExpiredAuctions() {
        auctionService.closeExpiredAuctions();
    }
}