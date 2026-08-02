package com.schwab.domain;

import com.schwab.dto.AnalyticsResponse;
import com.schwab.dto.ShortenRequest;
import com.schwab.dto.ShortenResponse;

public interface UrlShortenerServicePort {
    ShortenResponse shorten(ShortenRequest request);
    String resolve(String shortCode);
    AnalyticsResponse analytics();
}
