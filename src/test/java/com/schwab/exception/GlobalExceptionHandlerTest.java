package com.schwab.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldHandleInvalidUrl() {
        ResponseEntity<ApiErrorResponse> response = handler.handleInvalidUrl(new InvalidUrlException("Invalid URL"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("INVALID_URL", response.getBody().getCode());
        assertEquals("Invalid URL", response.getBody().getMessage());
    }

    @Test
    void shouldHandleShortCodeNotFound() {
        ResponseEntity<ApiErrorResponse> response = handler.handleShortCodeNotFound(new ShortCodeNotFoundException("Not found"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("SHORT_CODE_NOT_FOUND", response.getBody().getCode());
        assertEquals("Not found", response.getBody().getMessage());
    }

    @Test
    void shouldHandleShortCodeAlreadyExists() {
        ResponseEntity<ApiErrorResponse> response = handler.handleShortCodeAlreadyExists(
                new ShortCodeAlreadyExistsException("Conflict", null));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("SHORT_CODE_ALREADY_EXISTS", response.getBody().getCode());
        assertEquals("Conflict", response.getBody().getMessage());
    }
}
