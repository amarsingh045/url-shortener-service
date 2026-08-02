package com.schwab.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class AnalyticsResponse {
    @Schema(description = "The total number of shortened links", example = "10")
    private final long totalLinks;

    @Schema(description = "The total number of redirects performed", example = "25")
    private final long totalRedirects;

    public AnalyticsResponse(long totalLinks, long totalRedirects) {
        this.totalLinks = totalLinks;
        this.totalRedirects = totalRedirects;
    }

    public long getTotalLinks() {
        return totalLinks;
    }

    public long getTotalRedirects() {
        return totalRedirects;
    }
}
