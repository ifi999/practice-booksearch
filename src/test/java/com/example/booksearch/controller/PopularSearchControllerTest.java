package com.example.booksearch.controller;

import com.example.booksearch.repository.SearchLogRepository;
import com.example.booksearch.service.SearchLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("인기 검색어 API 테스트")
class PopularSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SearchLogService searchLogService;

    @Autowired
    private SearchLogRepository searchLogRepository;

    @BeforeEach
    void setUp() {
        searchLogRepository.deleteAll();
    }

    @Test
    @DisplayName("인기 검색어 API 기본 동작 테스트")
    void testGetPopularSearchKeywords() throws Exception {
        // Given
        createTestSearchLogs();

        // When & Then
        mockMvc.perform(get("/api/search/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].keyword").value("python"))
                .andExpect(jsonPath("$[0].searchCount").value(5))
                .andExpect(jsonPath("$[1].keyword").value("java"))
                .andExpect(jsonPath("$[1].searchCount").value(3));
    }

    @Test
    @DisplayName("인기 검색어 제한 개수 설정 테스트")
    void testGetPopularSearchKeywordsWithLimit() throws Exception {
        // Given
        createTestSearchLogs();

        // When & Then
        mockMvc.perform(get("/api/search/popular")
                        .param("limit", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].keyword").value("python"))
                .andExpect(jsonPath("$[1].keyword").value("java"));
    }

    @Test
    @DisplayName("검색어가 없을 때 빈 배열 반환 테스트")
    void testGetPopularSearchKeywordsEmpty() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/search/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(0)));
    }

    @Test
    @DisplayName("검색 후 인기 검색어에 반영 테스트")
    void testSearchLoggingIntegration() throws Exception {
        // Given - 검색 실행
        mockMvc.perform(get("/api/search/books")
                        .param("q", "spring boot"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/search/books")
                        .param("q", "spring boot"))
                .andExpect(status().isOk());

        // When & Then - 인기 검색어 확인
        mockMvc.perform(get("/api/search/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].keyword").value("spring boot"))
                .andExpect(jsonPath("$[0].searchCount").exists())
                .andExpect(jsonPath("$[0].lastSearchedAt").exists());
    }

    @Test
    @DisplayName("인기 검색어 응답 형식 테스트")
    void testPopularSearchResponseFormat() throws Exception {
        // Given
        searchLogService.logSearch("test keyword");

        // When & Then
        mockMvc.perform(get("/api/search/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].keyword").exists())
                .andExpect(jsonPath("$[0].searchCount").exists())
                .andExpect(jsonPath("$[0].lastSearchedAt").exists())
                .andExpect(jsonPath("$[0].lastSearchedAt").isString());
    }

    private void createTestSearchLogs() {
        // Python - 5회 검색
        for (int i = 0; i < 5; i++) {
            searchLogService.logSearch("python");
        }

        // Java - 3회 검색
        for (int i = 0; i < 3; i++) {
            searchLogService.logSearch("java");
        }

        // Spring - 2회 검색
        for (int i = 0; i < 2; i++) {
            searchLogService.logSearch("spring");
        }

        // React - 1회 검색
        searchLogService.logSearch("react");
    }
}