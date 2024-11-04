package com.artztall.auction_service.service;

import com.artztall.auction_service.dto.AuctionCreateDTO;
import com.artztall.auction_service.dto.BidDTO;
import com.artztall.auction_service.model.Auction;

import java.util.List;

public interface AuctionService {
    Auction createAuction(AuctionCreateDTO auctionDTO);
    Auction placeBid(BidDTO bidDTO);
    Auction getAuction(String id);
    List<Auction> getActiveAuctions();
    List<Auction> getAuctionsByArtist(String artistId);
    void closeExpiredAuctions();
    void cancelAuction(String auctionId);
}