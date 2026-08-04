package com.schwab.application;

public interface UrlShortenerUseCase {
    CreateShortUrlResult shorten(CreateShortUrlCommand command);
    String resolve(String shortCode);
    GetAnalyticsResult analytics();
}
