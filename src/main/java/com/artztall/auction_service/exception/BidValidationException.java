package com.artztall.auction_service.exception;

import org.springframework.http.HttpStatus;

public class BidValidationException extends AuctionBaseException {
    public BidValidationException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY, "BID_VALIDATION_ERROR");
    }

    public static class InsufficientBidAmountException extends BidValidationException {
        public InsufficientBidAmountException(String currentPrice, String bidAmount) {
            super("Bid amount " + bidAmount + " is less than current price " + currentPrice);
        }
    }

    public static class BidOnOwnAuctionException extends BidValidationException {
        public BidOnOwnAuctionException() {
            super("Users cannot bid on their own auction");
        }
    }

    public static class AuctionNotActiveException extends BidValidationException {
        public AuctionNotActiveException() {
            super("Cannot place bid on inactive auction");
        }
    }
}
