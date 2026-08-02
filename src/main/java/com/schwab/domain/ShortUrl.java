package com.schwab.domain;

public class ShortUrl {
    private final String shortCode;
    private final String longUrl;
    private long redirectCount;

    public ShortUrl(String shortCode, String longUrl) {
        this.shortCode = shortCode;
        this.longUrl = longUrl;
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

    public void setRedirectCount(long redirectCount) {
        this.redirectCount = redirectCount;
    }

    public void incrementRedirectCount() {
        this.redirectCount++;
    }
}
