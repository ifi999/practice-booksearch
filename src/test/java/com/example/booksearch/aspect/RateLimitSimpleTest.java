package com.example.booksearch.aspect;

import com.example.booksearch.annotation.RateLimit;
import com.example.booksearch.exception.RateLimitExceededException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Rate Limit 기본 기능 테스트")
class RateLimitSimpleTest {

    @Test
    @DisplayName("Bucket4j 기본 동작 테스트")
    void bucketBasicTest() {
        // 1분당 2회 제한
        Bandwidth limit = Bandwidth.classic(2, Refill.intervally(2, Duration.ofMinutes(1)));
        Bucket bucket = Bucket.builder().addLimit(limit).build();

        // 첫 번째, 두 번째 요청은 성공
        assertDoesNotThrow(() -> {
            if (!bucket.tryConsume(1)) {
                throw new RateLimitExceededException("Rate limit exceeded");
            }
        });
        
        assertDoesNotThrow(() -> {
            if (!bucket.tryConsume(1)) {
                throw new RateLimitExceededException("Rate limit exceeded");
            }
        });

        // 세 번째 요청은 실패
        assertThatThrownBy(() -> {
            if (!bucket.tryConsume(1)) {
                throw new RateLimitExceededException("Rate limit exceeded");
            }
        }).isInstanceOf(RateLimitExceededException.class);
    }

    @Test
    @DisplayName("Rate Limit 어노테이션 타입 검증")
    void rateLimitAnnotationTypes() {
        RateLimit.RateLimitType[] types = RateLimit.RateLimitType.values();
        
        assert types.length == 3;
        assert java.util.Arrays.asList(types).contains(RateLimit.RateLimitType.BOOK_READ);
        assert java.util.Arrays.asList(types).contains(RateLimit.RateLimitType.SEARCH_BASIC);
        assert java.util.Arrays.asList(types).contains(RateLimit.RateLimitType.SEARCH_POPULAR);
    }
}