package com.example.booksearch.aspect;

import com.example.booksearch.annotation.RateLimit;
import com.example.booksearch.config.RateLimitConfig;
import com.example.booksearch.exception.RateLimitExceededException;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.redis.jedis.cas.JedisBasedProxyManager;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@ConditionalOnProperty(name = "rate-limit.enabled", havingValue = "true", matchIfMissing = false)
public class RateLimitAspect {

    private final RateLimitConfig rateLimitConfig;
    private final JedisBasedProxyManager proxyManager;

    public RateLimitAspect(RateLimitConfig rateLimitConfig, JedisBasedProxyManager proxyManager) {
        this.rateLimitConfig = rateLimitConfig;
        this.proxyManager = proxyManager;
    }

    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String clientIP = getClientIP();
        String key = generateKey(rateLimit, clientIP);
        
        Bucket bucket = getBucket(rateLimit.value(), key);
        
        if (bucket.tryConsume(1)) {
            return joinPoint.proceed();
        } else {
            throw new RateLimitExceededException("Rate limit exceeded for IP: " + clientIP);
        }
    }

    private String getClientIP() {
        ServletRequestAttributes requestAttributes = 
            (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }
        
        return request.getRemoteAddr();
    }

    private String generateKey(RateLimit rateLimit, String clientIP) {
        String prefix = rateLimit.keyPrefix().isEmpty() ? 
            rateLimit.value().name().toLowerCase() : rateLimit.keyPrefix();
        return "rate_limit:" + prefix + ":" + clientIP;
    }

    private Bucket getBucket(RateLimit.RateLimitType type, String key) {
        return switch (type) {
            case BOOK_READ -> rateLimitConfig.createBookReadBucket(proxyManager, key);
            case SEARCH_BASIC -> rateLimitConfig.createSearchBucket(proxyManager, key);
            case SEARCH_POPULAR -> rateLimitConfig.createPopularSearchBucket(proxyManager, key);
        };
    }
}