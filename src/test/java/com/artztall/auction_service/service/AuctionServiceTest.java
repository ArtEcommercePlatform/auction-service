package com.artztall.auction_service.service;

import com.artztall.auction_service.dto.AuctionCreateDTO;
import com.artztall.auction_service.dto.BidDTO;
import com.artztall.auction_service.exception.AuctionException;
import com.artztall.auction_service.exception.BidValidationException;
import com.artztall.auction_service.model.Auction;
import com.artztall.auction_service.model.AuctionStatus;
import com.artztall.auction_service.model.Bid;
import com.artztall.auction_service.model.PaymentStatus;
import com.artztall.auction_service.repository.AuctionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuctionServiceTest {

    @Mock
    private AuctionRepository auctionRepository;

    @InjectMocks
    private AuctionServiceImpl auctionService;

    private Auction createSampleAuction() {
        Auction auction = new Auction();
        auction.setId(UUID.randomUUID().toString());
        auction.setTitle("Test Artwork");
        auction.setArtistId("artist-1");
        auction.setStartingPrice(100.0);
        auction.setCurrentPrice(100.0);
        auction.setStartTime(LocalDateTime.now().minusDays(1));
        auction.setEndTime(LocalDateTime.now().plusDays(1));
        auction.setAuctionStatus(AuctionStatus.ACTIVE);
        return auction;
    }

    @Nested
    @DisplayName("Auction Creation Tests")
    class AuctionCreationTests {
        @Test
        @DisplayName("Should create a valid auction successfully")
        void testCreateAuction() {
            // Arrange
            AuctionCreateDTO auctionDTO = new AuctionCreateDTO();
            auctionDTO.setTitle("Test Artwork");
            auctionDTO.setArtistId("artist-1");
            auctionDTO.setStartingPrice(100.0);
            auctionDTO.setStartTime(LocalDateTime.now());
            auctionDTO.setEndTime(LocalDateTime.now().plusDays(7));

            // Mock repository save
            when(auctionRepository.save(any(Auction.class))).thenAnswer(invocation -> {
                Auction savedAuction = invocation.getArgument(0);
                savedAuction.setId(UUID.randomUUID().toString());
                return savedAuction;
            });

            // Act
            Auction createdAuction = auctionService.createAuction(auctionDTO);

            // Assert
            assertNotNull(createdAuction);
            assertEquals(auctionDTO.getTitle(), createdAuction.getTitle());
            assertEquals(AuctionStatus.ACTIVE, createdAuction.getAuctionStatus());
            assertEquals(PaymentStatus.PENDING, createdAuction.getPaymentStatus());
            verify(auctionRepository).save(any(Auction.class));
        }

        @Test
        @DisplayName("Should throw exception when start time is after end time")
        void testCreateAuctionWithInvalidTimes() {
            // Arrange
            AuctionCreateDTO auctionDTO = new AuctionCreateDTO();
            auctionDTO.setStartTime(LocalDateTime.now().plusDays(2));
            auctionDTO.setEndTime(LocalDateTime.now());

            // Act & Assert
            assertThrows(AuctionException.class, () -> auctionService.createAuction(auctionDTO));
        }
    }

    @Nested
    @DisplayName("Bid Placement Tests")
    class BidPlacementTests {
        @Test
        @DisplayName("Should place a valid bid successfully")
        void testPlaceBid() {
            // Arrange
            Auction auction = createSampleAuction();
            BidDTO bidDTO = new BidDTO();
            bidDTO.setAuctionId(auction.getId());
            bidDTO.setUserId("user-1");
            bidDTO.setAmount(150.0);
            bidDTO.setBidTime(LocalDateTime.now());

            // Mock repository methods
            when(auctionRepository.findById(auction.getId())).thenReturn(Optional.of(auction));
            when(auctionRepository.save(any(Auction.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            Auction updatedAuction = auctionService.placeBid(bidDTO);

            // Assert
            assertNotNull(updatedAuction);
            assertEquals(150.0, updatedAuction.getCurrentPrice());
            assertEquals(1, updatedAuction.getBids().size());
            verify(auctionRepository).save(auction);
        }

        @Test
        @DisplayName("Should throw exception for bid lower than current price")
        void testPlaceBidLowerThanCurrentPrice() {
            // Arrange
            Auction auction = createSampleAuction();
            BidDTO bidDTO = new BidDTO();
            bidDTO.setAuctionId(auction.getId());
            bidDTO.setUserId("user-1");
            bidDTO.setAmount(50.0); // Lower than current price

            // Mock repository method
            when(auctionRepository.findById(auction.getId())).thenReturn(Optional.of(auction));

            // Act & Assert
            assertThrows(BidValidationException.class, () -> auctionService.placeBid(bidDTO));
        }
    }

    @Nested
    @DisplayName("Auction Closure Tests")
    class AuctionClosureTests {
        @Test
        @DisplayName("Should close expired auctions")
        void testCloseExpiredAuctions() {
            // Arrange
            Auction expiredAuction1 = createSampleAuction();
            expiredAuction1.setEndTime(LocalDateTime.now().minusDays(1));

            Bid highestBid = new Bid();
            highestBid.setUserId("winner-1");
            highestBid.setAmount(200.0);
            expiredAuction1.addBid(highestBid);

            List<Auction> expiredAuctions = List.of(expiredAuction1);

            // Mock repository methods
            when(auctionRepository.findExpiredActiveAuctions(any())).thenReturn(expiredAuctions);
            when(auctionRepository.saveAll(anyList())).thenReturn(expiredAuctions);

            // Act
            auctionService.closeExpiredAuctions();

            // Assert
            verify(auctionRepository).saveAll(expiredAuctions);
            assertEquals(AuctionStatus.COMPLETED, expiredAuction1.getAuctionStatus());
            assertEquals("winner-1", expiredAuction1.getWinnerId());
        }
    }

    @Nested
    @DisplayName("Auction Management Tests")
    class AuctionManagementTests {
        @Test
        @DisplayName("Should cancel a pending or active auction")
        void testCancelAuction() {
            // Arrange
            Auction auction = createSampleAuction();

            // Mock repository methods
            when(auctionRepository.findById(auction.getId())).thenReturn(Optional.of(auction));
            when(auctionRepository.save(any(Auction.class))).thenReturn(auction);

            // Act
            auctionService.cancelAuction(auction.getId());

            // Assert
            assertEquals(AuctionStatus.CANCELLED, auction.getAuctionStatus());
            verify(auctionRepository).save(auction);
        }

        @Test
        @DisplayName("Should extend auction time")
        void testExtendAuctionTime() {
            // Arrange
            Auction auction = createSampleAuction();
            LocalDateTime originalEndTime = auction.getEndTime();
            long extensionMinutes = 30;

            // Mock repository methods
            when(auctionRepository.findById(auction.getId())).thenReturn(Optional.of(auction));
            when(auctionRepository.save(any(Auction.class))).thenReturn(auction);

            // Act
            auctionService.extendAuctionTime(auction.getId(), extensionMinutes);

            // Assert
            assertEquals(originalEndTime.plusMinutes(extensionMinutes), auction.getEndTime());
            verify(auctionRepository).save(auction);
        }
    }
}