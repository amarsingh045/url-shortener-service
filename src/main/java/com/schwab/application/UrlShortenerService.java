package com.schwab.application;

import com.schwab.domain.ShortUrl;
import com.schwab.domain.ShortUrlRepositoryPort;
import com.schwab.dto.AnalyticsResponse;
import com.schwab.dto.ShortenRequest;
import com.schwab.dto.ShortenResponse;
import com.schwab.exception.InvalidUrlException;
import com.schwab.exception.ShortCodeAlreadyExistsException;
import com.schwab.exception.ShortCodeNotFoundException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

@Service
public class UrlShortenerService implements UrlShortenerUseCase {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int MAX_SHORT_CODE_GENERATION_ATTEMPTS = 5;
    private final ShortUrlRepositoryPort repository;
    private final Counter shortenCounter;
    private final Counter resolveCounter;
    private final Counter analyticsCounter;

    public UrlShortenerService(ShortUrlRepositoryPort repository, MeterRegistry meterRegistry) {
        this.repository = repository;
        this.shortenCounter = meterRegistry.counter("url.shortener.shorten.requests");
        this.resolveCounter = meterRegistry.counter("url.shortener.resolve.requests");
        this.analyticsCounter = meterRegistry.counter("url.shortener.analytics.requests");
    }

    @Override
    @CircuitBreaker(name = "shortenerCircuitBreaker", fallbackMethod = "fallbackShorten")
    @Bulkhead(name = "shortenerBulkhead")
    @Retry(name = "shortenerRetry")
    public ShortenResponse shorten(ShortenRequest request) {
        shortenCounter.increment();
        validateUrl(request.getLongUrl());
        ShortCodeAlreadyExistsException lastCollision = null;
        for (int attempt = 1; attempt <= MAX_SHORT_CODE_GENERATION_ATTEMPTS; attempt++) {
            String shortCode = generateUniqueShortCode();
            ShortUrl shortUrl = new ShortUrl(shortCode, request.getLongUrl());
            try {
                repository.save(shortUrl);
                return new ShortenResponse(shortCode, request.getLongUrl());
            } catch (ShortCodeAlreadyExistsException ex) {
                lastCollision = ex;
            }
        }
        throw new ShortCodeAlreadyExistsException("Unable to allocate a unique short code", lastCollision);
    }

    @Override
    @CircuitBreaker(name = "shortenerCircuitBreaker", fallbackMethod = "fallbackResolve")
    @Bulkhead(name = "shortenerBulkhead")
    @Retry(name = "shortenerRetry")
    public String resolve(String shortCode) {
        resolveCounter.increment();
        ShortUrl shortUrl = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new ShortCodeNotFoundException("Short code not found"));
        shortUrl.incrementRedirectCount();
        repository.save(shortUrl);
        return shortUrl.getLongUrl();
    }

    @Override
    @CircuitBreaker(name = "shortenerCircuitBreaker", fallbackMethod = "fallbackAnalytics")
    @Bulkhead(name = "shortenerBulkhead")
    @Retry(name = "shortenerRetry")
    public AnalyticsResponse analytics() {
        analyticsCounter.increment();
        long totalLinks = repository.count();
        long totalRedirects = 0;
        for (ShortUrl shortUrl : repository.findAll()) {
            totalRedirects += shortUrl.getRedirectCount();
        }
        return new AnalyticsResponse(totalLinks, totalRedirects);
    }

    public ShortenResponse fallbackShorten(ShortenRequest request, Throwable throwable) {
        if (throwable instanceof InvalidUrlException invalidUrlException) {
            throw invalidUrlException;
        }
        throw new InvalidUrlException("Service temporarily unavailable");
    }

    public String fallbackResolve(String shortCode, Throwable throwable) {
        if (throwable instanceof ShortCodeNotFoundException shortCodeNotFoundException) {
            throw shortCodeNotFoundException;
        }
        throw new ShortCodeNotFoundException("Service temporarily unavailable");
    }

    public AnalyticsResponse fallbackAnalytics(Throwable throwable) {
        return new AnalyticsResponse(0, 0);
    }

    private void validateUrl(String url) {
        try {
            URI uri = new URI(url);
            if (uri.getScheme() == null || uri.getHost() == null) {
                throw new InvalidUrlException("Invalid URL");
            }
        } catch (URISyntaxException e) {
            throw new InvalidUrlException("Invalid URL");
        }
    }

    private String generateUniqueShortCode() {
        String shortCode;
        do {
            shortCode = generateRandomCode();
        } while (repository.existsByShortCode(shortCode));
        return shortCode;
    }

    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            builder.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return builder.toString();
    }
}
