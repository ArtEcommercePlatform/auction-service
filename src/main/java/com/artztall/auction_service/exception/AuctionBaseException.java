package com.artztall.auction_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuctionBaseException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    public AuctionBaseException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
}