package com.schwab.infrastructure.mapper;

import com.schwab.domain.ShortUrl;
import com.schwab.dto.ShortenRequest;
import com.schwab.dto.ShortenResponse;
import com.schwab.infrastructure.persistence.ShortUrlEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ShortUrlMapper {
    ShortUrlEntity toEntity(ShortUrl shortUrl);

    ShortUrl toDomain(ShortUrlEntity entity);

    ShortenResponse toResponse(ShortUrl shortUrl);

    @Mapping(target = "shortCode", ignore = true)
    @Mapping(target = "redirectCount", ignore = true)
    ShortUrlEntity toEntity(ShortenRequest request);
}
