package com.artztall.auction_service.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalAuctionExceptionHandler {
    @ExceptionHandler(AuctionBaseException.class)
    public ResponseEntity<ErrorResponse> handleAuctionException(AuctionBaseException ex) {
        ErrorResponse error = new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Getter
    public static class ErrorResponse {
        private String errorCode;
        private String message;
        private LocalDateTime timestamp;

        public ErrorResponse(String errorCode, String message, LocalDateTime now) {
        }
    }
}