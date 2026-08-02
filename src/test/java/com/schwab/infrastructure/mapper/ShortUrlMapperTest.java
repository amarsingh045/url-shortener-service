package com.schwab.infrastructure.mapper;

import com.schwab.domain.ShortUrl;
import com.schwab.dto.ShortenRequest;
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
    void shouldMapRequestToEntityWithIgnoredFields() {
        ShortenRequest request = new ShortenRequest();
        request.setLongUrl("https://example.com/b");

        ShortUrlEntity entity = mapper.toEntity(request);
        assertNotNull(entity);
        assertNull(entity.getShortCode());
        assertEquals("https://example.com/b", entity.getLongUrl());
        assertEquals(0, entity.getRedirectCount());
    }
}
