package com.artztall.auction_service.service;

import com.artztall.auction_service.dto.AuctionCreateDTO;
import com.artztall.auction_service.dto.BidDTO;
import com.artztall.auction_service.dto.CompletedAuctionDTO;
import com.artztall.auction_service.model.Auction;

import java.util.List;

public interface AuctionService {
    Auction createAuction(AuctionCreateDTO auctionDTO);
    Auction placeBid(BidDTO bidDTO);
    Auction getAuctionById(String auctionId);
    List<Auction> getActiveAuctions();
    List<Auction> getAuctionsByArtist(String artistId);
    List<Auction> searchAuctions(String keyword);
    void closeExpiredAuctions();
    void cancelAuction(String auctionId);
    Auction updateAuctionDetails(String auctionId, AuctionCreateDTO updateDTO);
    void extendAuctionTime(String auctionId, long extensionMinutes);
    List<CompletedAuctionDTO> getCompletedAuctions();
    List<CompletedAuctionDTO> getCompletedAuctionsByWinnerId(String winnerId);
    List<BidDTO> getBidHistoryByAuctionId(String auctionId);
}