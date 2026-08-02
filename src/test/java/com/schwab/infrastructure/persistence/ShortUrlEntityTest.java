package com.schwab.infrastructure.persistence;

import com.schwab.domain.ShortUrl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShortUrlEntityTest {

    @Test
    void shouldConvertFromDomainAndBack() {
        ShortUrl domain = new ShortUrl("abc123", "https://example.com/a");
        domain.setRedirectCount(3);

        ShortUrlEntity entity = ShortUrlEntity.fromDomain(domain);
        assertEquals("abc123", entity.getShortCode());
        assertEquals("https://example.com/a", entity.getLongUrl());
        assertEquals(3, entity.getRedirectCount());

        ShortUrl mapped = entity.toDomain();
        assertEquals("abc123", mapped.getShortCode());
        assertEquals("https://example.com/a", mapped.getLongUrl());
        assertEquals(3, mapped.getRedirectCount());
    }

    @Test
    void shouldSupportSettersAndZeroRedirectDomainConversion() {
        ShortUrlEntity entity = new ShortUrlEntity();
        entity.setShortCode("xyz999");
        entity.setLongUrl("https://example.com/b");
        entity.setRedirectCount(0);

        assertEquals("xyz999", entity.getShortCode());
        assertEquals("https://example.com/b", entity.getLongUrl());
        assertEquals(0, entity.getRedirectCount());

        ShortUrl mapped = entity.toDomain();
        assertEquals(0, mapped.getRedirectCount());
    }
}
