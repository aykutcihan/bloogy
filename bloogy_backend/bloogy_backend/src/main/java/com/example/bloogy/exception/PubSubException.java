package com.example.bloogy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class PubSubException extends RuntimeException {

    public PubSubException(String message) {
        super(message);
    }

    public PubSubException(String message, Throwable cause) {
        super(message, cause);
    }
}

