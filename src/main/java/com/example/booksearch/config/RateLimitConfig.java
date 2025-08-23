package com.example.booksearch.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.github.bucket4j.redis.jedis.cas.JedisBasedProxyManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPool;

import java.time.Duration;

@Configuration
@ConditionalOnProperty(name = "rate-limit.enabled", havingValue = "true", matchIfMissing = false)
public class RateLimitConfig {

    @Bean
    public JedisPool jedisPool(RedisConnectionFactory redisConnectionFactory) {
        JedisConnectionFactory jedisConnectionFactory = (JedisConnectionFactory) redisConnectionFactory;
        return new JedisPool(jedisConnectionFactory.getHostName(), jedisConnectionFactory.getPort());
    }

    @Bean
    public JedisBasedProxyManager proxyManager(JedisPool jedisPool) {
        return new JedisBasedProxyManager(jedisPool);
    }

    public Bucket createBookReadBucket(JedisBasedProxyManager proxyManager, String key) {
        return proxyManager.builder()
                .build(key, () -> createBookReadConfiguration());
    }

    public Bucket createSearchBucket(JedisBasedProxyManager proxyManager, String key) {
        return proxyManager.builder()
                .build(key, () -> createSearchConfiguration());
    }

    public Bucket createPopularSearchBucket(JedisBasedProxyManager proxyManager, String key) {
        return proxyManager.builder()
                .build(key, () -> createPopularSearchConfiguration());
    }

    private io.github.bucket4j.BucketConfiguration createBookReadConfiguration() {
        return io.github.bucket4j.BucketConfiguration.builder()
                .addLimit(Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1))))
                .build();
    }

    private io.github.bucket4j.BucketConfiguration createSearchConfiguration() {
        return io.github.bucket4j.BucketConfiguration.builder()
                .addLimit(Bandwidth.classic(50, Refill.intervally(50, Duration.ofMinutes(1))))
                .build();
    }

    private io.github.bucket4j.BucketConfiguration createPopularSearchConfiguration() {
        return io.github.bucket4j.BucketConfiguration.builder()
                .addLimit(Bandwidth.classic(20, Refill.intervally(20, Duration.ofMinutes(1))))
                .build();
    }
}