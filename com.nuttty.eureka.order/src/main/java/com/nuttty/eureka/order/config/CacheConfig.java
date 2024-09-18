package com.nuttty.eureka.order.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        // 설정 구성을 먼저 진행한다.
        // Redis 를 이용해서 Spring Cache 를 사용할 때 Redis 관련 설정을 모아두는 클래스
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues() // null 캐싱 여부(null 캐싱하지 않는다)
                .entryTtl(Duration.ofSeconds(120)) // 기본 캐시 유지 시간 (Time to Live)
                .computePrefixWith(CacheKeyPrefix.simple()) // 캐시를 구분하는 접두사 설정
                // 캐시에 저장할 값을 어떻게 직렬화 / 역직렬화 할 것인지 설정
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.java()));

        // "directions" 캐시 설정
        // 변동 되지 않는 정보(거리 계산, 시간 계산)이기 때문에 30일 설정
        RedisCacheConfiguration directionsCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                // "directions" 캐시에 대해 TTL을 30일 설정
                .entryTtl(Duration.ofDays(14))
                .computePrefixWith(CacheKeyPrefix.simple())
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        // 캐시 이름별로 설정을 지정
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("directions", directionsCacheConfig);

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig) // 기본 설정
                .withInitialCacheConfigurations(cacheConfigurations) // 커스텀 캐시 설정 추가
                .build();    }
}
