package com.artztall.auction_service.exception;

import org.springframework.http.HttpStatus;

public class AuctionException extends AuctionBaseException {
    public AuctionException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "AUCTION_ERROR");
    }

    public static class AuctionNotFoundException extends AuctionException {
        public AuctionNotFoundException(String auctionId) {
            super("Auction not found with ID: " + auctionId);
        }
    }

    public static class AuctionAlreadyExistsException extends AuctionException {
        public AuctionAlreadyExistsException(String auctionId) {
            super("Auction already exists with ID: " + auctionId);
        }
    }

    public static class AuctionStatusException extends AuctionException {
        public AuctionStatusException(String currentStatus, String expectedStatus) {
            super("Invalid auction status. Current: " + currentStatus + ", Expected: " + expectedStatus);
        }
    }

    public static class AuctionTimeException extends AuctionException {
        public AuctionTimeException(String message) {
            super(message);
        }
    }
}

