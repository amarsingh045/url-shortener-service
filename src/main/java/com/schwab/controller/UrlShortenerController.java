package com.schwab.controller;

import com.schwab.domain.UrlShortenerServicePort;
import com.schwab.dto.AnalyticsResponse;
import com.schwab.dto.ShortenRequest;
import com.schwab.dto.ShortenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api")
@Tag(name = "URL Shortener", description = "Shorten URLs and inspect analytics")
public class UrlShortenerController {

    private final UrlShortenerServicePort urlShortenerService;

    public UrlShortenerController(UrlShortenerServicePort urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @Operation(summary = "Create a short URL", description = "Creates a short URL for a valid long URL")
    @ApiResponse(responseCode = "201", description = "Short URL created", content = @Content(schema = @Schema(implementation = ShortenResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid URL payload")
    @PostMapping("/shorten")
    public ResponseEntity<ShortenResponse> shorten(@RequestBody ShortenRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(urlShortenerService.shorten(request));
    }

    @Operation(summary = "Redirect to the original URL", description = "Resolves a short code and redirects to the original URL")
    @ApiResponse(responseCode = "301", description = "Redirect to the original URL")
    @ApiResponse(responseCode = "404", description = "Short code not found")
    @GetMapping("/{shortCode}")
    public RedirectView redirect(@PathVariable String shortCode) {
        String target = urlShortenerService.resolve(shortCode);
        RedirectView redirectView = new RedirectView(target, true);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        return redirectView;
    }

    @Operation(summary = "Get analytics", description = "Returns aggregate analytics for created links")
    @ApiResponse(responseCode = "200", description = "Analytics returned", content = @Content(schema = @Schema(implementation = AnalyticsResponse.class)))
    @GetMapping("/analytics")
    public ResponseEntity<AnalyticsResponse> analytics() {
        return ResponseEntity.ok(urlShortenerService.analytics());
    }
}
