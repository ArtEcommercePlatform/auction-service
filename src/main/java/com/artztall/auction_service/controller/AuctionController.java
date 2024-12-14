package com.artztall.auction_service.controller;

import com.artztall.auction_service.dto.AuctionCreateDTO;
import com.artztall.auction_service.dto.BidDTO;
import com.artztall.auction_service.dto.CompletedAuctionDTO;
import com.artztall.auction_service.model.Auction;
import com.artztall.auction_service.service.AuctionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auctions")
@RequiredArgsConstructor
@Tag(name = "Auction Controller", description = "API for managing auctions")
public class AuctionController {
    private final AuctionService auctionService;

    @PostMapping
    @Operation(summary = "Create a new auction", description = "Creates a new auction with the provided details.")
    @ApiResponse(
            responseCode = "201",
            description = "Auction created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Auction.class))
    )
    public ResponseEntity<Auction> createAuction(
            @Valid @RequestBody AuctionCreateDTO auctionDTO
    ) {
        Auction createdAuction = auctionService.createAuction(auctionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAuction);
    }

    @PostMapping("/bid")
    @Operation(summary = "Place a bid", description = "Places a bid on an existing auction.")
    @ApiResponse(
            responseCode = "200",
            description = "Bid placed successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Auction.class))
    )
    public ResponseEntity<Auction> placeBid(
            @Valid @RequestBody BidDTO bidDTO
    ) {
        Auction updatedAuction = auctionService.placeBid(bidDTO);
        return ResponseEntity.ok(updatedAuction);
    }

    @GetMapping("/{auctionId}")
    @Operation(summary = "Get auction details", description = "Retrieves details of an auction by ID.")
    @ApiResponse(
            responseCode = "200",
            description = "Auction details retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Auction.class))
    )
    public ResponseEntity<Auction> getAuction(
            @Parameter(description = "ID of the auction to retrieve") @PathVariable String auctionId
    ) {
        Auction auction = auctionService.getAuctionById(auctionId);
        return ResponseEntity.ok(auction);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active auctions", description = "Retrieves all active auctions.")
    @ApiResponse(
            responseCode = "200",
            description = "Active auctions retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))
    )
    public ResponseEntity<List<Auction>> getActiveAuctions() {
        List<Auction> activeAuctions = auctionService.getActiveAuctions();
        return ResponseEntity.ok(activeAuctions);
    }

    @GetMapping("/artist/{artistId}")
    @Operation(summary = "Get auctions by artist", description = "Retrieves all auctions for a specific artist.")
    @ApiResponse(
            responseCode = "200",
            description = "Auctions retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))
    )
    public ResponseEntity<List<Auction>> getAuctionsByArtist(
            @Parameter(description = "ID of the artist") @PathVariable String artistId
    ) {
        List<Auction> artistAuctions = auctionService.getAuctionsByArtist(artistId);
        return ResponseEntity.ok(artistAuctions);
    }

    @PutMapping("/{auctionId}")
    @Operation(summary = "Update auction details", description = "Updates the details of an existing auction.")
    @ApiResponse(
            responseCode = "200",
            description = "Auction updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Auction.class))
    )
    public ResponseEntity<Auction> updateAuction(
            @Parameter(description = "ID of the auction to update") @PathVariable String auctionId,
            @Valid @RequestBody AuctionCreateDTO updateDTO
    ) {
        Auction updatedAuction = auctionService.updateAuctionDetails(auctionId, updateDTO);
        return ResponseEntity.ok(updatedAuction);
    }

    @DeleteMapping("/{auctionId}")
    @Operation(summary = "Cancel auction", description = "Cancels an auction by ID.")
    @ApiResponse(
            responseCode = "204",
            description = "Auction cancelled successfully"
    )
    public ResponseEntity<Void> cancelAuction(
            @Parameter(description = "ID of the auction to cancel") @PathVariable String auctionId
    ) {
        auctionService.cancelAuction(auctionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{auctionId}/extend")
    @Operation(summary = "Extend auction time", description = "Extends the duration of an auction.")
    @ApiResponse(
            responseCode = "200",
            description = "Auction time extended successfully"
    )
    public ResponseEntity<Void> extendAuctionTime(
            @Parameter(description = "ID of the auction to extend") @PathVariable String auctionId,
            @Parameter(description = "Number of minutes to extend the auction") @RequestParam long extensionMinutes
    ) {
        auctionService.extendAuctionTime(auctionId, extensionMinutes);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/completed")
    @Operation(summary = "Get completed auctions", description = "Retrieves all completed auctions.")
    @ApiResponse(
            responseCode = "200",
            description = "Completed auctions retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))
    )
    public ResponseEntity<List<CompletedAuctionDTO>> getCompletedAuctions() {
        List<CompletedAuctionDTO> completedAuctions = auctionService.getCompletedAuctions();
        return ResponseEntity.ok(completedAuctions);
    }


    @GetMapping("/completed/winner/{winnerId}")
    @Operation(
            summary = "Get Completed Auctions by Winner ID",
            description = "Retrieve all completed auctions won by a specific user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved completed auctions"),
                    @ApiResponse(responseCode = "404", description = "No auctions found for the winner")
            }
    )
    public ResponseEntity<List<CompletedAuctionDTO>> getCompletedAuctionsByWinnerId(
            @Parameter(description = "ID of the winner", required = true)
            @PathVariable String winnerId
    ) {
        List<CompletedAuctionDTO> completedAuctions = auctionService.getCompletedAuctionsByWinnerId(winnerId);
        return ResponseEntity.ok(completedAuctions);
    }

    @GetMapping("/{auctionId}/bid-history")
    @Operation(
            summary = "Get Bid History for an Auction",
            description = "Retrieve all bids for a specific auction",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved bid history"),
                    @ApiResponse(responseCode = "404", description = "Auction not found")
            }
    )
    public ResponseEntity<List<BidDTO>> getBidHistoryByAuctionId(
            @Parameter(description = "ID of the auction", required = true)
            @PathVariable String auctionId
    ) {
        List<BidDTO> bidHistory = auctionService.getBidHistoryByAuctionId(auctionId);
        return ResponseEntity.ok(bidHistory);
    }

}
