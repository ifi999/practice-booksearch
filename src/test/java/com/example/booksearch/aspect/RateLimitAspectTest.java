package com.example.booksearch.aspect;

import com.example.booksearch.annotation.RateLimit;
import com.example.booksearch.config.RateLimitConfig;
import com.example.booksearch.config.TestRedisConfig;
import com.example.booksearch.exception.RateLimitExceededException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@Import(TestRedisConfig.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "rate-limit.enabled=true",
    "spring.data.redis.host=localhost",
    "spring.data.redis.port=6370",
    "spring.data.redis.database=1"
})
@DisplayName("Rate Limit Aspect 테스트")
class RateLimitAspectTest {

    @Autowired
    private TestRateLimitService testService;

    @Test
    @DisplayName("Rate Limit 범위 내에서는 정상 처리")
    void rateLimitWithinBounds() {
        // 첫 번째 호출은 성공해야 함
        assertDoesNotThrow(() -> testService.limitedMethod());
    }

    @Test
    @DisplayName("Rate Limit 초과 시 예외 발생")
    void rateLimitExceeded() {
        // Rate limit을 소진시키기 위해 반복 호출
        for (int i = 0; i < 21; i++) { // SEARCH_POPULAR는 20회 제한
            try {
                testService.popularSearchMethod();
            } catch (RateLimitExceededException e) {
                // 예상되는 예외
                break;
            }
        }
        
        // 한 번 더 호출하면 예외가 발생해야 함
        assertThatThrownBy(() -> testService.popularSearchMethod())
                .isInstanceOf(RateLimitExceededException.class)
                .hasMessageContaining("Rate limit exceeded");
    }

    // 테스트용 서비스 클래스
    @org.springframework.stereotype.Service
    static class TestRateLimitService {
        
        @RateLimit(RateLimit.RateLimitType.BOOK_READ)
        public String limitedMethod() {
            return "success";
        }
        
        @RateLimit(RateLimit.RateLimitType.SEARCH_POPULAR)
        public String popularSearchMethod() {
            return "popular search result";
        }
    }
}