package com.example.booksearch.exception;

/**
 * Rate limit이 초과되었을 때 발생하는 예외
 */
public class RateLimitExceededException extends RuntimeException {
    
    public RateLimitExceededException(String message) {
        super(message);
    }
    
    public RateLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}