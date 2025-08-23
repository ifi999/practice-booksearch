package com.example.booksearch.config;

/*
 * Redis 캐시 설정 클래스 (Redis 의존성 추가 시 활성화)
 * 
 * Redis로 전환하려면:
 * 1. build.gradle에 spring-boot-starter-data-redis 의존성 추가
 * 2. 이 클래스의 주석 해제
 * 3. application-redis.properties 프로파일 활성화
 */

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.context.annotation.Profile;
// import org.springframework.data.redis.cache.RedisCacheConfiguration;
// import org.springframework.data.redis.cache.RedisCacheManager;
// import org.springframework.data.redis.connection.RedisConnectionFactory;
// import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
// import org.springframework.data.redis.serializer.RedisSerializationContext;
// 
// import java.time.Duration;
// import java.util.HashMap;
// import java.util.Map;
// 
// @Configuration
// @Profile("redis") // Redis 프로파일일 때만 활성화
// public class RedisCacheConfig {
// 
//     @Bean
//     public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
//         RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
//                 .entryTtl(Duration.ofMinutes(30))
//                 .serializeValuesWith(RedisSerializationContext.SerializationPair
//                         .fromSerializer(new GenericJackson2JsonRedisSerializer()));
// 
//         Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
//         
//         // 검색 결과 캐시 - 30분 TTL
//         cacheConfigurations.put("searchResults", defaultConfig
//                 .entryTtl(Duration.ofMinutes(30)));
//         
//         // 인기 검색어 캐시 - 10분 TTL (더 빈번한 업데이트)
//         cacheConfigurations.put("popularKeywords", defaultConfig
//                 .entryTtl(Duration.ofMinutes(10)));
// 
//         return RedisCacheManager.builder(connectionFactory)
//                 .cacheDefaults(defaultConfig)
//                 .withInitialCacheConfigurations(cacheConfigurations)
//                 .build();
//     }
// }