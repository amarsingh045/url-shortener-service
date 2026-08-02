package com.schwab.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class ShortenRequest {
    @Schema(description = "The long URL to shorten", example = "https://example.com/products", requiredMode = Schema.RequiredMode.REQUIRED)
    private String longUrl;

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }
}