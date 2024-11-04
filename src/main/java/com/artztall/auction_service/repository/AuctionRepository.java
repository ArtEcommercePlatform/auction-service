package com.artztall.auction_service.repository;

import com.artztall.auction_service.model.Auction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AuctionRepository extends MongoRepository<Auction, String> {
    List<Auction> findByStatus(String status);
    List<Auction> findByArtistId(String artistId);
    List<Auction> findByEndTimeBefore(LocalDateTime endTime);
    List<Auction> findByStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
            LocalDateTime now, LocalDateTime now2);
}