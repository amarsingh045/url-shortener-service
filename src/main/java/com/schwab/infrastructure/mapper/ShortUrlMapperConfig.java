package com.schwab.infrastructure.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShortUrlMapperConfig {

    @Bean
    @ConditionalOnMissingBean(ShortUrlMapper.class)
    public ShortUrlMapper shortUrlMapper() {
        return Mappers.getMapper(ShortUrlMapper.class);
    }
}
