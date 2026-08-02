package com.schwab.exception;

public class ShortCodeAlreadyExistsException extends RuntimeException {
    public ShortCodeAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}