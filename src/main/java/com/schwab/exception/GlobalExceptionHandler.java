package com.schwab.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidUrl(InvalidUrlException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse("INVALID_URL", ex.getMessage()));
    }

    @ExceptionHandler(ShortCodeNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleShortCodeNotFound(ShortCodeNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse("SHORT_CODE_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(ShortCodeAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleShortCodeAlreadyExists(ShortCodeAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse("SHORT_CODE_ALREADY_EXISTS", ex.getMessage()));
    }
}
