package com.schwab.application;

import com.schwab.domain.ShortUrl;
import com.schwab.domain.ShortUrlRepositoryPort;
import com.schwab.dto.AnalyticsResponse;
import com.schwab.exception.InvalidUrlException;
import com.schwab.exception.ShortCodeAlreadyExistsException;
import com.schwab.exception.ShortCodeNotFoundException;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceTest {

    @Mock
    private ShortUrlRepositoryPort repository;

    private UrlShortenerService service;

    @BeforeEach
    void setUp() {
        service = new UrlShortenerService(repository, new SimpleMeterRegistry());
    }

    @Test
    void shouldThrowInvalidUrlForMalformedInput() {
        CreateShortUrlCommand command = new CreateShortUrlCommand("not-a-valid-url");

        assertThrows(InvalidUrlException.class, () -> service.shorten(command));
    }

    @Test
    void shouldThrowNotFoundWhenShortCodeDoesNotExist() {
        when(repository.findByShortCode("missing")).thenReturn(Optional.empty());

        assertThrows(ShortCodeNotFoundException.class, () -> service.resolve("missing"));
    }

    @Test
    void shouldAggregateAnalytics() {
        ShortUrl first = new ShortUrl("a1", "https://example.com/1");
        first.setRedirectCount(2);
        ShortUrl second = new ShortUrl("b2", "https://example.com/2");
        second.setRedirectCount(5);

        when(repository.count()).thenReturn(2L);
        when(repository.findAll()).thenReturn(List.of(first, second));

        GetAnalyticsResult response = service.analytics();

        assertEquals(2L, response.totalLinks());
        assertEquals(7L, response.totalRedirects());
    }

    @Test
    void fallbackShortenShouldThrowInvalidUrlException() {
        CreateShortUrlCommand command = new CreateShortUrlCommand("https://example.com");

        InvalidUrlException ex = assertThrows(InvalidUrlException.class,
            () -> service.fallbackShorten(command, new RuntimeException("boom")));

        assertEquals("Service temporarily unavailable", ex.getMessage());
    }

    @Test
    void fallbackResolveShouldThrowNotFoundException() {
        ShortCodeNotFoundException ex = assertThrows(ShortCodeNotFoundException.class,
                () -> service.fallbackResolve("abc123", new RuntimeException("boom")));

        assertEquals("Service temporarily unavailable", ex.getMessage());
    }

    @Test
    void fallbackAnalyticsShouldReturnZeroedMetrics() {
        GetAnalyticsResult response = service.fallbackAnalytics(new RuntimeException("boom"));

        assertEquals(0L, response.totalLinks());
        assertEquals(0L, response.totalRedirects());
    }

    @Test
    void shouldCreateShortUrlForValidRequest() {
        when(repository.existsByShortCode(any())).thenReturn(false);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CreateShortUrlCommand command = new CreateShortUrlCommand("https://example.com/ok");

        CreateShortUrlResult response = service.shorten(command);

        assertEquals("https://example.com/ok", response.longUrl());
    }

    @Test
    void shouldRetryWhenShortCodeCollisionOccurs() {
        when(repository.existsByShortCode(any())).thenReturn(false);
        when(repository.save(any()))
                .thenThrow(new ShortCodeAlreadyExistsException("Short code already exists", null))
                .thenThrow(new ShortCodeAlreadyExistsException("Short code already exists", null))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateShortUrlCommand command = new CreateShortUrlCommand("https://example.com/retry");

        CreateShortUrlResult response = service.shorten(command);

        assertEquals("https://example.com/retry", response.longUrl());
        verify(repository, times(3)).save(any());
    }

    @Test
    void shouldFailAfterMaxCollisionRetries() {
        when(repository.existsByShortCode(any())).thenReturn(false);
        when(repository.save(any())).thenThrow(new ShortCodeAlreadyExistsException("Short code already exists", null));

        CreateShortUrlCommand command = new CreateShortUrlCommand("https://example.com/retry-fail");

        ShortCodeAlreadyExistsException ex = assertThrows(ShortCodeAlreadyExistsException.class,
            () -> service.shorten(command));

        assertEquals("Unable to allocate a unique short code", ex.getMessage());
        verify(repository, times(5)).save(any());
    }
}
