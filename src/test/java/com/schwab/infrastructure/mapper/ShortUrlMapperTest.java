package com.schwab.infrastructure.mapper;

import com.schwab.domain.ShortUrl;
import com.schwab.dto.ShortenResponse;
import com.schwab.infrastructure.persistence.ShortUrlEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class ShortUrlMapperTest {

    @Autowired
    private ShortUrlMapper mapper;

    @Test
    void shouldMapDomainToEntityAndBack() {
        ShortUrl domain = new ShortUrl("abc123", "https://example.com/a");
        domain.setRedirectCount(7);

        ShortUrlEntity entity = mapper.toEntity(domain);
        assertEquals("abc123", entity.getShortCode());
        assertEquals("https://example.com/a", entity.getLongUrl());
        assertEquals(7, entity.getRedirectCount());

        ShortUrl mappedBack = mapper.toDomain(entity);
        assertEquals("abc123", mappedBack.getShortCode());
        assertEquals("https://example.com/a", mappedBack.getLongUrl());
        assertEquals(7, mappedBack.getRedirectCount());
    }

    @Test
    void shouldMapDomainToResponse() {
        ShortUrl domain = new ShortUrl("xyz789", "https://example.com/c");

        ShortenResponse response = mapper.toResponse(domain);

        assertNotNull(response);
        assertEquals("xyz789", response.getShortCode());
        assertEquals("https://example.com/c", response.getLongUrl());
    }

    @Test
    void shouldReturnNullWhenMappingNullInputs() {
        assertNull(mapper.toEntity((ShortUrl) null));
        assertNull(mapper.toDomain(null));
        assertNull(mapper.toResponse(null));
    }
}
