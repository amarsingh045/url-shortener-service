package com.schwab.service;

import com.schwab.domain.ShortUrl;
import com.schwab.domain.ShortUrlRepositoryPort;
import com.schwab.dto.AnalyticsResponse;
import com.schwab.dto.ShortenRequest;
import com.schwab.dto.ShortenResponse;
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
        ShortenRequest request = new ShortenRequest();
        request.setLongUrl("not-a-valid-url");

        assertThrows(InvalidUrlException.class, () -> service.shorten(request));
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

        AnalyticsResponse response = service.analytics();

        assertEquals(2L, response.getTotalLinks());
        assertEquals(7L, response.getTotalRedirects());
    }

    @Test
    void fallbackShortenShouldThrowInvalidUrlException() {
        ShortenRequest request = new ShortenRequest();
        request.setLongUrl("https://example.com");

        InvalidUrlException ex = assertThrows(InvalidUrlException.class,
                () -> service.fallbackShorten(request, new RuntimeException("boom")));

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
        AnalyticsResponse response = service.fallbackAnalytics(new RuntimeException("boom"));

        assertEquals(0L, response.getTotalLinks());
        assertEquals(0L, response.getTotalRedirects());
    }

    @Test
    void shouldCreateShortUrlForValidRequest() {
        when(repository.existsByShortCode(any())).thenReturn(false);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ShortenRequest request = new ShortenRequest();
        request.setLongUrl("https://example.com/ok");

        ShortenResponse response = service.shorten(request);

        assertEquals("https://example.com/ok", response.getLongUrl());
    }

    @Test
    void shouldRetryWhenShortCodeCollisionOccurs() {
        when(repository.existsByShortCode(any())).thenReturn(false);
        when(repository.save(any()))
                .thenThrow(new ShortCodeAlreadyExistsException("Short code already exists", null))
                .thenThrow(new ShortCodeAlreadyExistsException("Short code already exists", null))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ShortenRequest request = new ShortenRequest();
        request.setLongUrl("https://example.com/retry");

        ShortenResponse response = service.shorten(request);

        assertEquals("https://example.com/retry", response.getLongUrl());
        verify(repository, times(3)).save(any());
    }

    @Test
    void shouldFailAfterMaxCollisionRetries() {
        when(repository.existsByShortCode(any())).thenReturn(false);
        when(repository.save(any())).thenThrow(new ShortCodeAlreadyExistsException("Short code already exists", null));

        ShortenRequest request = new ShortenRequest();
        request.setLongUrl("https://example.com/retry-fail");

        ShortCodeAlreadyExistsException ex = assertThrows(ShortCodeAlreadyExistsException.class,
                () -> service.shorten(request));

        assertEquals("Unable to allocate a unique short code", ex.getMessage());
        verify(repository, times(5)).save(any());
    }
}
