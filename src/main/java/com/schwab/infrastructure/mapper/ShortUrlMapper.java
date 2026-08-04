package com.schwab.infrastructure.mapper;

import com.schwab.domain.ShortUrl;
import com.schwab.dto.ShortenResponse;
import com.schwab.infrastructure.persistence.ShortUrlEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ShortUrlMapper {
    ShortUrlEntity toEntity(ShortUrl shortUrl);

    ShortUrl toDomain(ShortUrlEntity entity);

    ShortenResponse toResponse(ShortUrl shortUrl);
}
