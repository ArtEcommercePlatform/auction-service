package com.artztall.auction_service.repository;

import com.artztall.auction_service.model.Auction;
import com.artztall.auction_service.model.AuctionStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuctionRepository extends MongoRepository<Auction, String> {
    @Query("{ 'startTime': { $lte: ?0 }, 'endTime': { $gte: ?0 }, 'auctionStatus': 'ACTIVE' }")
    List<Auction> findActiveAuctions(LocalDateTime currentTime);

    List<Auction> findByArtistIdAndAuctionStatusIn(String artistId, List<AuctionStatus> statuses);

    @Query("{ 'endTime': { $lt: ?0 }, 'auctionStatus': 'ACTIVE' }")
    List<Auction> findExpiredActiveAuctions(LocalDateTime currentTime);

    @Query("{ $or: [ " +
            "{ 'title': { $regex: ?0, $options: 'i' } }, " +
            "{ 'description': { $regex: ?0, $options: 'i' } } " +
            "] }")
    List<Auction> searchAuctionsByKeyword(String keyword);
}