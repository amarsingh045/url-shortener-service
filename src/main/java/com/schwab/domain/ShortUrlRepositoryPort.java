package com.schwab.domain;

import java.util.Optional;

public interface ShortUrlRepositoryPort {
    ShortUrl save(ShortUrl shortUrl);
    Optional<ShortUrl> findByShortCode(String shortCode);
    boolean existsByShortCode(String shortCode);
    long count();
    Iterable<ShortUrl> findAll();
}
