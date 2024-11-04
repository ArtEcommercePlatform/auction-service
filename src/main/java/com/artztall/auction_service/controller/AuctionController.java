package com.artztall.auction_service.controller;


import com.artztall.auction_service.dto.AuctionCreateDTO;
import com.artztall.auction_service.dto.BidDTO;
import com.artztall.auction_service.model.Auction;
import com.artztall.auction_service.service.AuctionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/auctions")
@RequiredArgsConstructor
public class AuctionController {
    private final AuctionService auctionService;

    @PostMapping
    public ResponseEntity<Auction> createAuction(@Valid @RequestBody AuctionCreateDTO auctionDTO) {
        return ResponseEntity.ok(auctionService.createAuction(auctionDTO));
    }

    @PostMapping("/bid")
    public ResponseEntity<Auction> placeBid(@Valid @RequestBody BidDTO bidDTO) {
        return ResponseEntity.ok(auctionService.placeBid(bidDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Auction> getAuction(@PathVariable String id) {
        return ResponseEntity.ok(auctionService.getAuction(id));
    }

    @GetMapping("/active")
    public ResponseEntity<List<Auction>> getActiveAuctions() {
        return ResponseEntity.ok(auctionService.getActiveAuctions());
    }

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<List<Auction>> getAuctionsByArtist(@PathVariable String artistId) {
        return ResponseEntity.ok(auctionService.getAuctionsByArtist(artistId));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelAuction(@PathVariable String id) {
        auctionService.cancelAuction(id);
        return ResponseEntity.ok().build();
    }
}

