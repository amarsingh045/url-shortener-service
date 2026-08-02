package com.schwab.infrastructure.persistence;

import com.schwab.domain.ShortUrl;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "short_urls", uniqueConstraints = @UniqueConstraint(columnNames = "short_code"))
public class ShortUrlEntity {
    @Id
    @Column(name = "short_code", nullable = false, unique = true)
    private String shortCode;
    @Column(name = "long_url", nullable = false, length = 2048)
    private String longUrl;
    @Column(name = "redirect_count", nullable = false)
    private long redirectCount;

    public ShortUrlEntity() {
    }

    public ShortUrlEntity(String shortCode, String longUrl, long redirectCount) {
        this.shortCode = shortCode;
        this.longUrl = longUrl;
        this.redirectCount = redirectCount;
    }

    public static ShortUrlEntity fromDomain(ShortUrl shortUrl) {
        return new ShortUrlEntity(shortUrl.getShortCode(), shortUrl.getLongUrl(), shortUrl.getRedirectCount());
    }

    public ShortUrl toDomain() {
        ShortUrl shortUrl = new ShortUrl(shortCode, longUrl);
        if (redirectCount > 0) {
            for (long i = 0; i < redirectCount; i++) {
                shortUrl.incrementRedirectCount();
            }
        }
        return shortUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public long getRedirectCount() {
        return redirectCount;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public void setRedirectCount(long redirectCount) {
        this.redirectCount = redirectCount;
    }
}
