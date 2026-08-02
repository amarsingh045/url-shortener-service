package com.schwab.infrastructure.persistence;

import com.schwab.domain.ShortUrl;
import com.schwab.domain.ShortUrlRepositoryPort;
import com.schwab.infrastructure.mapper.ShortUrlMapper;
import com.schwab.exception.ShortCodeAlreadyExistsException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class JpaShortUrlRepositoryAdapter implements ShortUrlRepositoryPort {
    private final ShortUrlEntityRepository repository;
    private final ShortUrlMapper mapper;
    private final ConcurrentHashMap<String, ShortUrl> cache = new ConcurrentHashMap<>();

    public JpaShortUrlRepositoryAdapter(ShortUrlEntityRepository repository, ShortUrlMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public ShortUrl save(ShortUrl shortUrl) {
        ShortUrlEntity entity = mapper.toEntity(shortUrl);
        ShortUrlEntity saved;
        try {
            saved = repository.save(entity);
        } catch (DataIntegrityViolationException ex) {
            throw new ShortCodeAlreadyExistsException("Short code already exists", ex);
        }
        ShortUrl domain = mapper.toDomain(saved);
        cache.put(domain.getShortCode(), domain);
        return domain;
    }

    @Override
    public Optional<ShortUrl> findByShortCode(String shortCode) {
        return Optional.ofNullable(cache.computeIfAbsent(shortCode, key -> repository.findById(key)
                .map(mapper::toDomain)
                .orElse(null)));
    }

    @Override
    public boolean existsByShortCode(String shortCode) {
        if (cache.containsKey(shortCode)) {
            return true;
        }
        boolean exists = repository.existsById(shortCode);
        if (exists) {
            repository.findById(shortCode).ifPresent(entity -> cache.putIfAbsent(shortCode, mapper.toDomain(entity)));
        }
        return exists;
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public Iterable<ShortUrl> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }
}
