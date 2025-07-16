package com.marketpulse.aggregator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(FmpApiException.class)
    public ResponseEntity<String> handleFmpApi(FmpApiException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(ex.getMessage());
    }
}
