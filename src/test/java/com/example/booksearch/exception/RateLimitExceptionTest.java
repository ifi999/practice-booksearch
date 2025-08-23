package com.example.booksearch.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Rate Limit 예외 테스트")
class RateLimitExceptionTest {

    @Test
    @DisplayName("RateLimitExceededException 메시지 테스트")
    void rateLimitExceptionMessage() {
        String expectedMessage = "Rate limit exceeded for IP: 192.168.1.1";
        
        RateLimitExceededException exception = new RateLimitExceededException(expectedMessage);
        
        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("RateLimitExceededException 원인과 함께 생성")
    void rateLimitExceptionWithCause() {
        String expectedMessage = "Rate limit exceeded";
        RuntimeException cause = new RuntimeException("Redis connection failed");
        
        RateLimitExceededException exception = new RateLimitExceededException(expectedMessage, cause);
        
        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
        assertThat(exception.getCause()).isEqualTo(cause);
    }
}