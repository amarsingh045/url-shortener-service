package com.schwab.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class ShortenResponse {
    @Schema(description = "The generated short code", example = "a1b2c3d4")
    private final String shortCode;

    @Schema(description = "The original long URL", example = "https://example.com/products")
    private final String longUrl;

    public ShortenResponse(String shortCode, String longUrl) {
        this.shortCode = shortCode;
        this.longUrl = longUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getLongUrl() {
        return longUrl;
    }
}