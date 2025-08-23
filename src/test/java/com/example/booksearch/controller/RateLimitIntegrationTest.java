package com.example.booksearch.controller;

import com.example.booksearch.config.TestRedisConfig;
import com.example.booksearch.domain.Book;
import com.example.booksearch.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestRedisConfig.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "rate-limit.enabled=true",
    "spring.data.redis.host=localhost",
    "spring.data.redis.port=6370",
    "spring.data.redis.database=1"
})
@Transactional
@DisplayName("Rate Limiting 통합 테스트")
class RateLimitIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        
        Book testBook = Book.builder()
                .isbn("9788966262281")
                .title("Clean Code")
                .author("로버트 C. 마틴")
                .publisher("인사이트")
                .publicationDate(LocalDate.of(2013, 12, 24))
                .build();
        
        bookRepository.save(testBook);
    }

    @Test
    @DisplayName("정상 범위 내 요청은 성공")
    void normalRequestSucceeds() throws Exception {
        mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("인기 검색어 API Rate Limit 테스트")
    void popularSearchRateLimit() throws Exception {
        // 20회까지는 성공해야 함
        for (int i = 0; i < 20; i++) {
            mockMvc.perform(get("/api/search/popular")
                            .param("limit", "5")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        // 21번째 요청은 Rate Limit 초과로 실패해야 함
        mockMvc.perform(get("/api/search/popular")
                        .param("limit", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.status").value(429))
                .andExpect(jsonPath("$.error").value("Too Many Requests"))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Rate limit exceeded")));
    }

    @Test
    @DisplayName("다른 IP에서는 독립적인 Rate Limit 적용")
    void differentIpIndependentRateLimit() throws Exception {
        // 첫 번째 IP에서 20회 요청
        for (int i = 0; i < 20; i++) {
            mockMvc.perform(get("/api/search/popular")
                            .param("limit", "5")
                            .header("X-Forwarded-For", "192.168.1.1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        // 두 번째 IP에서는 여전히 요청 가능해야 함
        mockMvc.perform(get("/api/search/popular")
                        .param("limit", "5")
                        .header("X-Forwarded-For", "192.168.1.2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}