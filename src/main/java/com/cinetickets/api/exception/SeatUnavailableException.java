package com.cinetickets.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class SeatUnavailableException extends RuntimeException {

    public SeatUnavailableException(String message) {
        super(message);
    }

    public SeatUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}