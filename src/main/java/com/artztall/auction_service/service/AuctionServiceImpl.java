package com.artztall.auction_service.service;

import com.artztall.auction_service.dto.AuctionCreateDTO;
import com.artztall.auction_service.dto.BidDTO;
import com.artztall.auction_service.model.Auction;
import com.artztall.auction_service.model.Bid;
import com.artztall.auction_service.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {
    private final AuctionRepository auctionRepository;

    @Override
    @Transactional
    public Auction createAuction(AuctionCreateDTO auctionDTO) {
        Auction auction = new Auction();
        auction.setTitle(auctionDTO.getTitle());
        auction.setDescription(auctionDTO.getDescription());
        auction.setPaintingId(auctionDTO.getPaintingId());
        auction.setArtistId(auctionDTO.getArtistId());
        auction.setStartingPrice(auctionDTO.getStartingPrice());
        auction.setCurrentPrice(auctionDTO.getStartingPrice());
        auction.setStartTime(auctionDTO.getStartTime());
        auction.setEndTime(auctionDTO.getEndTime());
        auction.setStatus("PENDING");
        auction.setCreatedAt(LocalDateTime.now());
        auction.setUpdatedAt(LocalDateTime.now());

        return auctionRepository.save(auction);
    }

    @Override
    @Transactional
    public Auction placeBid(BidDTO bidDTO) {
        Auction auction = auctionRepository.findById(bidDTO.getAuctionId())
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        validateBid(auction, bidDTO);

        Bid bid = new Bid();
        bid.setId(UUID.randomUUID().toString());
        bid.setAuctionId(auction.getId());
        bid.setUserId(bidDTO.getUserId());
        bid.setAmount(bidDTO.getAmount());
        bid.setBidTime(LocalDateTime.now());

        auction.addBid(bid);
        auction.setCurrentPrice(bidDTO.getAmount());
        auction.setUpdatedAt(LocalDateTime.now());

        return auctionRepository.save(auction);
    }

    private void validateBid(Auction auction, BidDTO bidDTO) {
        if (!"ACTIVE".equals(auction.getStatus())) {
            throw new RuntimeException("Auction is not active");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(auction.getStartTime()) || now.isAfter(auction.getEndTime())) {
            throw new RuntimeException("Auction is not within its time period");
        }

        if (bidDTO.getAmount().compareTo(auction.getCurrentPrice()) <= 0) {
            throw new RuntimeException("Bid amount must be higher than current price");
        }
    }

    @Override
    public Auction getAuction(String id) {
        return auctionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Auction not found"));
    }

    @Override
    public List<Auction> getActiveAuctions() {
        LocalDateTime now = LocalDateTime.now();
        return auctionRepository.findByStartTimeLessThanEqualAndEndTimeGreaterThanEqual(now, now);
    }

    @Override
    public List<Auction> getAuctionsByArtist(String artistId) {
        return auctionRepository.findByArtistId(artistId);
    }

    @Override
    @Transactional
    public void closeExpiredAuctions() {
        List<Auction> expiredAuctions = auctionRepository.findByEndTimeBefore(LocalDateTime.now());
        for (Auction auction : expiredAuctions) {
            if ("ACTIVE".equals(auction.getStatus())) {
                auction.setStatus("COMPLETED");
                if (auction.hasBids()) {
                    Bid highestBid = auction.getHighestBid();
                    auction.setWinnerId(highestBid.getUserId());
                }
                auction.setUpdatedAt(LocalDateTime.now());
                auctionRepository.save(auction);
            }
        }
    }


    @Override
    @Transactional
    public void cancelAuction(String auctionId) {
        Auction auction = getAuction(auctionId);
        if ("ACTIVE".equals(auction.getStatus()) || "PENDING".equals(auction.getStatus())) {
            auction.setStatus("CANCELLED");
            auction.setUpdatedAt(LocalDateTime.now());
            auctionRepository.save(auction);
        } else {
            throw new RuntimeException("Cannot cancel auction in current status");
        }
    }
}
