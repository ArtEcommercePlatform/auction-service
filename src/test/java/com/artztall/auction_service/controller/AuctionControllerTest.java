package com.artztall.auction_service.controller;


import com.artztall.auction_service.dto.AuctionCreateDTO;
import com.artztall.auction_service.dto.BidDTO;
import com.artztall.auction_service.dto.CompletedAuctionDTO;
import com.artztall.auction_service.model.Auction;
import com.artztall.auction_service.model.AuctionStatus;
import com.artztall.auction_service.model.PaymentStatus;
import com.artztall.auction_service.service.AuctionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuctionControllerTest {

    @Mock
    private AuctionService auctionService;

    @InjectMocks
    private AuctionController auctionController;

    private AuctionCreateDTO createSampleAuctionDTO() {
        AuctionCreateDTO dto = new AuctionCreateDTO();
        dto.setTitle("Test Artwork");
        dto.setArtistId("artist-1");
        dto.setStartingPrice(100.0);
        dto.setStartTime(LocalDateTime.now());
        dto.setEndTime(LocalDateTime.now().plusDays(7));
        dto.setPaintingUrl("http://test-painting.com");
        return dto;
    }

    private Auction createSampleAuction() {
        Auction auction = new Auction();
        auction.setId(UUID.randomUUID().toString());
        auction.setTitle("Test Artwork");
        auction.setArtistId("artist-1");
        auction.setAuctionStatus(AuctionStatus.ACTIVE);
        return auction;
    }

    @Nested
    @DisplayName("Auction Creation Tests")
    class AuctionCreationTests {
        @Test
        @DisplayName("Should create auction successfully")
        void testCreateAuction() {
            // Arrange
            AuctionCreateDTO auctionDTO = createSampleAuctionDTO();
            Auction createdAuction = createSampleAuction();

            when(auctionService.createAuction(auctionDTO)).thenReturn(createdAuction);

            // Act
            ResponseEntity<Auction> response = auctionController.createAuction(auctionDTO);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertEquals(createdAuction, response.getBody());
            verify(auctionService).createAuction(auctionDTO);
        }
    }

    @Nested
    @DisplayName("Bid Placement Tests")
    class BidPlacementTests {
        @Test
        @DisplayName("Should place bid successfully")
        void testPlaceBid() {
            // Arrange
            BidDTO bidDTO = new BidDTO();
            bidDTO.setAuctionId(UUID.randomUUID().toString());
            bidDTO.setUserId("user-1");
            bidDTO.setAmount(150.0);
            bidDTO.setBidTime(LocalDateTime.now());

            Auction updatedAuction = createSampleAuction();

            when(auctionService.placeBid(bidDTO)).thenReturn(updatedAuction);

            // Act
            ResponseEntity<Auction> response = auctionController.placeBid(bidDTO);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(updatedAuction, response.getBody());
            verify(auctionService).placeBid(bidDTO);
        }
    }

    @Nested
    @DisplayName("Auction Retrieval Tests")
    class AuctionRetrievalTests {
        @Test
        @DisplayName("Should retrieve auction by ID")
        void testGetAuction() {
            // Arrange
            String auctionId = UUID.randomUUID().toString();
            Auction auction = createSampleAuction();

            when(auctionService.getAuctionById(auctionId)).thenReturn(auction);

            // Act
            ResponseEntity<Auction> response = auctionController.getAuction(auctionId);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(auction, response.getBody());
            verify(auctionService).getAuctionById(auctionId);
        }

        @Test
        @DisplayName("Should retrieve active auctions")
        void testGetActiveAuctions() {
            // Arrange
            List<Auction> activeAuctions = List.of(createSampleAuction());

            when(auctionService.getActiveAuctions()).thenReturn(activeAuctions);

            // Act
            ResponseEntity<List<Auction>> response = auctionController.getActiveAuctions();

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(activeAuctions, response.getBody());
            verify(auctionService).getActiveAuctions();
        }

        @Test
        @DisplayName("Should retrieve auctions by artist")
        void testGetAuctionsByArtist() {
            // Arrange
            String artistId = "artist-1";
            List<Auction> artistAuctions = List.of(createSampleAuction());

            when(auctionService.getAuctionsByArtist(artistId)).thenReturn(artistAuctions);

            // Act
            ResponseEntity<List<Auction>> response = auctionController.getAuctionsByArtist(artistId);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(artistAuctions, response.getBody());
            verify(auctionService).getAuctionsByArtist(artistId);
        }
    }

    @Nested
    @DisplayName("Auction Management Tests")
    class AuctionManagementTests {
        @Test
        @DisplayName("Should update auction details")
        void testUpdateAuction() {
            // Arrange
            String auctionId = UUID.randomUUID().toString();
            AuctionCreateDTO updateDTO = createSampleAuctionDTO();
            Auction updatedAuction = createSampleAuction();

            when(auctionService.updateAuctionDetails(auctionId, updateDTO)).thenReturn(updatedAuction);

            // Act
            ResponseEntity<Auction> response = auctionController.updateAuction(auctionId, updateDTO);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(updatedAuction, response.getBody());
            verify(auctionService).updateAuctionDetails(auctionId, updateDTO);
        }

        @Test
        @DisplayName("Should cancel auction")
        void testCancelAuction() {
            // Arrange
            String auctionId = UUID.randomUUID().toString();
            doNothing().when(auctionService).cancelAuction(auctionId);

            // Act
            ResponseEntity<Void> response = auctionController.cancelAuction(auctionId);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            verify(auctionService).cancelAuction(auctionId);
        }

        @Test
        @DisplayName("Should extend auction time")
        void testExtendAuctionTime() {
            // Arrange
            String auctionId = UUID.randomUUID().toString();
            long extensionMinutes = 30;
            doNothing().when(auctionService).extendAuctionTime(auctionId, extensionMinutes);

            // Act
            ResponseEntity<Void> response = auctionController.extendAuctionTime(auctionId, extensionMinutes);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(auctionService).extendAuctionTime(auctionId, extensionMinutes);
        }
    }

    @Nested
    @DisplayName("Completed Auction Tests")
    class CompletedAuctionTests {
        @Test
        @DisplayName("Should retrieve completed auctions")
        void testGetCompletedAuctions() {
            // Arrange
            CompletedAuctionDTO completedAuction = new CompletedAuctionDTO(
                    UUID.randomUUID().toString(),
                    "Test Artwork",
                    "Description",
                    "http://test-painting.com",
                    200.0,
                    "winner-1",
                    LocalDateTime.now(),
                    PaymentStatus.PENDING
            );
            List<CompletedAuctionDTO> completedAuctions = List.of(completedAuction);

            when(auctionService.getCompletedAuctions()).thenReturn(completedAuctions);

            // Act
            ResponseEntity<List<CompletedAuctionDTO>> response = auctionController.getCompletedAuctions();

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(completedAuctions, response.getBody());
            verify(auctionService).getCompletedAuctions();
        }

        @Test
        @DisplayName("Should retrieve completed auctions by winner ID")
        void testGetCompletedAuctionsByWinnerId() {
            // Arrange
            String winnerId = "winner-1";
            CompletedAuctionDTO completedAuction = new CompletedAuctionDTO(
                    UUID.randomUUID().toString(),
                    "Test Artwork",
                    "Description",
                    "http://test-painting.com",
                    200.0,
                    winnerId,
                    LocalDateTime.now(),
                    PaymentStatus.PENDING
            );
            List<CompletedAuctionDTO> completedAuctions = List.of(completedAuction);

            when(auctionService.getCompletedAuctionsByWinnerId(winnerId)).thenReturn(completedAuctions);

            // Act
            ResponseEntity<List<CompletedAuctionDTO>> response = auctionController.getCompletedAuctionsByWinnerId(winnerId);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(completedAuctions, response.getBody());
            verify(auctionService).getCompletedAuctionsByWinnerId(winnerId);
        }

        @Test
        @DisplayName("Should retrieve bid history for an auction")
        void testGetBidHistoryByAuctionId() {
            // Arrange
            String auctionId = UUID.randomUUID().toString();
            BidDTO bidDTO = new BidDTO();
            bidDTO.setAuctionId(auctionId);
            bidDTO.setUserId("user-1");
            bidDTO.setAmount(150.0);
            bidDTO.setBidTime(LocalDateTime.now());

            List<BidDTO> bidHistory = List.of(bidDTO);

            when(auctionService.getBidHistoryByAuctionId(auctionId)).thenReturn(bidHistory);

            // Act
            ResponseEntity<List<BidDTO>> response = auctionController.getBidHistoryByAuctionId(auctionId);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(bidHistory, response.getBody());
            verify(auctionService).getBidHistoryByAuctionId(auctionId);
        }
    }
}
