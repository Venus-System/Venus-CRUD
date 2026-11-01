package com.venus.crud.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.LoggingCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.redis.cache.BatchStrategies;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@EnableCaching(order = Ordered.HIGHEST_PRECEDENCE)
public class CacheConfig implements CachingConfigurer {

    public static final String USER_FAVORITES_CACHE = "favoritos-do-usuario";
    public static final String USER_FAVORITES_COUNT_CACHE = "contagem-de-favoritos-do-usuario";

    private static final int SCAN_BATCH_SIZE = 1000;

    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory connectionFactory,
            @Value("${spring.cache.redis.time-to-live:6h}") Duration timeToLive) {
        // allEntries usa SCAN: o KEYS padrao e bloqueado em Redis gerenciado com ACL restrita
        RedisCacheWriter writer = RedisCacheWriter.nonLockingRedisCacheWriter(
                connectionFactory, BatchStrategies.scan(SCAN_BATCH_SIZE));
        RedisCacheConfiguration config = RedisCacheConfiguration
                .defaultCacheConfig(getClass().getClassLoader())
                .entryTtl(timeToLive);
        return RedisCacheManager.builder(writer).cacheDefaults(config).build();
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new LoggingCacheErrorHandler();
    }
}
