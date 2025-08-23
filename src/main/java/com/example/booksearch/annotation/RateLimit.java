package com.example.booksearch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    
    /**
     * Rate limit type
     */
    RateLimitType value() default RateLimitType.BOOK_READ;
    
    /**
     * Custom key prefix for rate limiting (optional)
     */
    String keyPrefix() default "";
    
    enum RateLimitType {
        BOOK_READ,
        SEARCH_BASIC,
        SEARCH_POPULAR
    }
}