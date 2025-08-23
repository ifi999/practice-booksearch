package com.example.booksearch.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@TestConfiguration
public class RateLimitTestConfig {

    @Bean
    @Primary
    public TestRateLimitManager testRateLimitManager() {
        return new TestRateLimitManager();
    }

    public static class TestRateLimitManager {
        private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

        public Bucket getBucket(String key, io.github.bucket4j.BucketConfiguration configuration) {
            return buckets.computeIfAbsent(key, k -> Bucket.builder()
                    .addLimit(configuration.getBandwidths()[0])
                    .build());
        }

        public void clear() {
            buckets.clear();
        }
    }
}