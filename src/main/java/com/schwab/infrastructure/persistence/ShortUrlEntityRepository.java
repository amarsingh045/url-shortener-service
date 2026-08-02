package com.schwab.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortUrlEntityRepository extends JpaRepository<ShortUrlEntity, String> {
}
