package com.marketpulse.aggregator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class FmpApiException extends RuntimeException {
    public FmpApiException(String message) {
        super(message);
    }
    public FmpApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
