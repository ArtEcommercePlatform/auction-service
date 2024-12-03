package com.artztall.auction_service.exception;

import org.springframework.http.HttpStatus;

public class UserAuctionException extends AuctionBaseException {
    public UserAuctionException(String message) {
        super(message, HttpStatus.FORBIDDEN, "USER_AUCTION_ERROR");
    }

    public static class UnauthorizedAuctionAccessException extends UserAuctionException {
        public UnauthorizedAuctionAccessException() {
            super("User is not authorized to perform this action");
        }
    }

    public static class UserNotEligibleException extends UserAuctionException {
        public UserNotEligibleException(String reason) {
            super("User is not eligible: " + reason);
        }
    }
}
