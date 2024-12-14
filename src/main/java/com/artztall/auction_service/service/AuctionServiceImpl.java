package com.artztall.auction_service.service;

import com.artztall.auction_service.dto.AuctionCreateDTO;
import com.artztall.auction_service.dto.BidDTO;
import com.artztall.auction_service.dto.CompletedAuctionDTO;
import com.artztall.auction_service.exception.AuctionException;
import com.artztall.auction_service.exception.BidValidationException;
import com.artztall.auction_service.model.Auction;
import com.artztall.auction_service.model.AuctionStatus;
import com.artztall.auction_service.model.Bid;
import com.artztall.auction_service.model.PaymentStatus;
import com.artztall.auction_service.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {
    private static final long MAX_AUCTION_DURATION_DAYS = 30;

    private final AuctionRepository auctionRepository;

    @Override
    @Transactional
    public Auction createAuction(AuctionCreateDTO auctionDTO) {
        validateAuctionCreation(auctionDTO);

        Auction auction = new Auction();
        auction.setTitle(auctionDTO.getTitle());
        auction.setDescription(auctionDTO.getDescription());
        auction.setPaintingUrl(auctionDTO.getPaintingUrl());
        auction.setArtistId(auctionDTO.getArtistId());
        auction.setStartingPrice(auctionDTO.getStartingPrice());
        auction.setCurrentPrice(auctionDTO.getStartingPrice());
        auction.setStartTime(auctionDTO.getStartTime());
        auction.setPaymentStatus(PaymentStatus.PENDING);
        auction.setEndTime(calculateEndTime(auctionDTO));
        auction.setAuctionStatus(AuctionStatus.ACTIVE);

        log.info("Creating new auction for painting: {}", auctionDTO.getPaintingUrl());
        return auctionRepository.save(auction);
    }

    @Override
    @Transactional
    public Auction placeBid(BidDTO bidDTO) {
        Auction auction = getAuctionById(bidDTO.getAuctionId());
        validateBid(auction, bidDTO);

        Bid bid = createBid(auction, bidDTO);
        auction.addBid(bid);
        auction.setCurrentPrice(bidDTO.getAmount());

        log.info("Bid placed on auction {} by user {}", auction.getId(), bidDTO.getUserId());
        return auctionRepository.save(auction);
    }

    private void validateBid(Auction auction, BidDTO bidDTO) {
        LocalDateTime now = LocalDateTime.now();

        if (!AuctionStatus.ACTIVE.equals(auction.getAuctionStatus())) {
            throw new BidValidationException("Auction is not active");
        }

        if (now.isBefore(auction.getStartTime()) || now.isAfter(auction.getEndTime())) {
            throw new BidValidationException("Auction is not within its time period");
        }

        BigDecimal minBidAmount = BigDecimal.valueOf(auction.getCurrentPrice());
        if (BigDecimal.valueOf(bidDTO.getAmount()).compareTo(minBidAmount) < 0) {
            throw new BidValidationException("Bid must be at least " + minBidAmount);
        }
    }

    private Bid createBid(Auction auction, BidDTO bidDTO) {
        Bid bid = new Bid();
        bid.setId(UUID.randomUUID().toString());
        bid.setAuctionId(auction.getId());
        bid.setUserId(bidDTO.getUserId());
        bid.setAmount(bidDTO.getAmount());
        bid.setBidTime(LocalDateTime.now());
        return bid;
    }

    @Override
    @Transactional
    public void closeExpiredAuctions() {
        LocalDateTime now = LocalDateTime.now();
        List<Auction> expiredAuctions = auctionRepository.findExpiredActiveAuctions(now);

        expiredAuctions.forEach(auction -> {
            auction.setAuctionStatus(AuctionStatus.COMPLETED);
            if (auction.hasBids()) {
                Bid highestBid = auction.getHighestBid();
                auction.setWinnerId(highestBid.getUserId());
            }
            log.info("Closing auction: {}", auction.getId());
        });

        auctionRepository.saveAll(expiredAuctions);
    }


    private void validateAuctionCreation(AuctionCreateDTO auctionDTO) {
        if (auctionDTO.getStartTime().isAfter(auctionDTO.getEndTime())) {
            throw new AuctionException("Start time must be before end time");
        }
    }

    private LocalDateTime calculateEndTime(AuctionCreateDTO auctionDTO) {
        LocalDateTime proposedEndTime = auctionDTO.getEndTime();
        LocalDateTime maxAllowedEndTime = auctionDTO.getStartTime().plusDays(MAX_AUCTION_DURATION_DAYS);

        return proposedEndTime.isBefore(maxAllowedEndTime) ?
                proposedEndTime : maxAllowedEndTime;
    }

    // Placeholder implementations for other methods
    @Override
    public Auction getAuctionById(String auctionId) {
        return auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionException("Auction not found"));
    }

    @Override
    public List<Auction> getActiveAuctions() {
        return auctionRepository.findActiveAuctions(LocalDateTime.now());
    }

    @Override
    public List<Auction> getAuctionsByArtist(String artistId) {
        return auctionRepository.findByArtistIdAndAuctionStatusIn(
                artistId,
                List.of(AuctionStatus.PENDING, AuctionStatus.ACTIVE, AuctionStatus.COMPLETED)
        );
    }

    @Override
    public List<Auction> searchAuctions(String keyword) {
        return auctionRepository.searchAuctionsByKeyword(keyword);
    }

    @Override
    @Transactional
    public void cancelAuction(String auctionId) {
        Auction auction = getAuctionById(auctionId);
        if (List.of(AuctionStatus.ACTIVE, AuctionStatus.PENDING).contains(auction.getAuctionStatus())) {
            auction.setAuctionStatus(AuctionStatus.CANCELLED);
            auctionRepository.save(auction);
            log.info("Auction cancelled: {}", auctionId);
        } else {
            throw new AuctionException("Cannot cancel auction in current status");
        }
    }

    @Override
    public List<CompletedAuctionDTO> getCompletedAuctions() {
        return auctionRepository.findByAuctionStatus(AuctionStatus.COMPLETED)
                .stream()
                .map(this::convertToCompletedAuctionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CompletedAuctionDTO> getCompletedAuctionsByWinnerId(String winnerId) {
        return auctionRepository.findByAuctionStatusAndWinnerId(AuctionStatus.COMPLETED, winnerId)
                .stream()
                .map(this::convertToCompletedAuctionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BidDTO> getBidHistoryByAuctionId(String auctionId) {
        Auction auction = getAuctionById(auctionId);
        return auction.getBids().stream()
                .map(this::convertToBidDTO)
                .sorted(Comparator.comparing(BidDTO::getBidTime).reversed())
                .collect(Collectors.toList());
    }

    private BidDTO convertToBidDTO(Bid bid) {
        BidDTO bidDTO = new BidDTO();
        bidDTO.setAuctionId(bid.getAuctionId());
        bidDTO.setUserId(bid.getUserId());
        bidDTO.setAmount(bid.getAmount());
        bidDTO.setBidTime(bid.getBidTime());
        return bidDTO;
    }

    private CompletedAuctionDTO convertToCompletedAuctionDTO(Auction auction) {
        CompletedAuctionDTO dto = new CompletedAuctionDTO();
        dto.setId(auction.getId());
        dto.setTitle(auction.getTitle());
        dto.setDescription(auction.getDescription());
        dto.setPaintingUrl(auction.getPaintingUrl());
        dto.setFinalPrice(auction.getCurrentPrice());
        dto.setWinnerId(auction.getWinnerId());
        dto.setEndTime(auction.getEndTime());
        dto.setPaymentStatus(auction.getPaymentStatus());
        return dto;
    }

    @Override
    @Transactional
    public Auction updateAuctionDetails(String auctionId, AuctionCreateDTO updateDTO) {
        Auction auction = getAuctionById(auctionId);

        if (!AuctionStatus.PENDING.equals(auction.getAuctionStatus())) {
            throw new AuctionException("Only pending auctions can be updated");
        }

        auction.setTitle(updateDTO.getTitle());
        auction.setDescription(updateDTO.getDescription());
        auction.setStartingPrice(updateDTO.getStartingPrice());
        auction.setStartTime(updateDTO.getStartTime());
        auction.setEndTime(calculateEndTime(updateDTO));

        log.info("Updating auction details: {}", auctionId);
        return auctionRepository.save(auction);
    }

    @Override
    @Transactional
    public void extendAuctionTime(String auctionId, long extensionMinutes) {
        Auction auction = getAuctionById(auctionId);

        if (!AuctionStatus.ACTIVE.equals(auction.getAuctionStatus())) {
            throw new AuctionException("Only active auctions can be extended");
        }

        LocalDateTime newEndTime = auction.getEndTime().plusMinutes(extensionMinutes);
        auction.setEndTime(newEndTime);

        log.info("Extending auction time: {} by {} minutes", auctionId, extensionMinutes);
        auctionRepository.save(auction);
    }
}