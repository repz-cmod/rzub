package com.github.rzub.config;

import com.google.common.cache.CacheBuilder;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheConfiguration {

    @Bean("cacheManager")
    @Primary
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager() {
            @Override
            protected Cache createConcurrentMapCache(final String name) {
                return new ConcurrentMapCache(name, CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).maximumSize(100).build().asMap(), false);
            }
        };

        return cacheManager;
    }

    @Bean("donatorTicketCacheManager")
    public CacheManager donatorTicketCacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager() {
            @Override
            protected Cache createConcurrentMapCache(final String name) {
                return new ConcurrentMapCache(name, CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).maximumSize(100).build().asMap(), false);
            }
        };
        cacheManager.setCacheNames(Collections.singletonList("TICKET"));
        return cacheManager;
    }

}
