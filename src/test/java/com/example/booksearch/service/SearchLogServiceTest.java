package com.example.booksearch.service;

import com.example.booksearch.domain.SearchLog;
import com.example.booksearch.repository.SearchLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("검색어 로깅 서비스 테스트")
class SearchLogServiceTest {

    @Autowired
    private SearchLogService searchLogService;

    @Autowired
    private SearchLogRepository searchLogRepository;

    @BeforeEach
    void setUp() {
        searchLogRepository.deleteAll();
    }

    @Test
    @DisplayName("새로운 검색어 로깅 테스트")
    void testLogNewSearchKeyword() {
        // Given
        String keyword = "java";

        // When
        searchLogService.logSearch(keyword);

        // Then
        Optional<SearchLog> savedLog = searchLogRepository.findByKeyword(keyword);
        assertThat(savedLog).isPresent();
        assertThat(savedLog.get().getKeyword()).isEqualTo(keyword);
        assertThat(savedLog.get().getSearchCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("기존 검색어 카운트 증가 테스트")
    void testLogExistingSearchKeyword() {
        // Given
        String keyword = "python";
        searchLogService.logSearch(keyword);

        // When
        searchLogService.logSearch(keyword);

        // Then
        Optional<SearchLog> savedLog = searchLogRepository.findByKeyword(keyword);
        assertThat(savedLog).isPresent();
        assertThat(savedLog.get().getSearchCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("대소문자 정규화 테스트")
    void testKeywordNormalization() {
        // Given
        String keyword1 = "Java";
        String keyword2 = "JAVA";
        String keyword3 = "java";

        // When
        searchLogService.logSearch(keyword1);
        searchLogService.logSearch(keyword2);
        searchLogService.logSearch(keyword3);

        // Then
        Optional<SearchLog> savedLog = searchLogRepository.findByKeyword("java");
        assertThat(savedLog).isPresent();
        assertThat(savedLog.get().getSearchCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("빈 문자열 및 null 키워드 처리 테스트")
    void testEmptyAndNullKeywords() {
        // Given & When
        searchLogService.logSearch(null);
        searchLogService.logSearch("");
        searchLogService.logSearch("   ");

        // Then
        List<SearchLog> allLogs = searchLogRepository.findAll();
        assertThat(allLogs).isEmpty();
    }

    @Test
    @DisplayName("Top 검색어 조회 테스트")
    void testGetTopSearchKeywords() {
        // Given
        createTestSearchLogs();

        // When
        List<SearchLog> topSearches = searchLogService.getTopSearchKeywords(3);

        // Then
        assertThat(topSearches).hasSize(3);
        assertThat(topSearches.get(0).getKeyword()).isEqualTo("python");
        assertThat(topSearches.get(0).getSearchCount()).isEqualTo(5);
        assertThat(topSearches.get(1).getKeyword()).isEqualTo("java");
        assertThat(topSearches.get(1).getSearchCount()).isEqualTo(3);
        assertThat(topSearches.get(2).getKeyword()).isEqualTo("spring");
        assertThat(topSearches.get(2).getSearchCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("인기 검색어 최소 카운트 필터 테스트")
    void testGetPopularSearchKeywordsWithMinCount() {
        // Given
        createTestSearchLogs();

        // When
        List<SearchLog> popularSearches = searchLogService.getPopularSearchKeywords(3, 10);

        // Then
        assertThat(popularSearches).hasSize(2);
        assertThat(popularSearches.get(0).getKeyword()).isEqualTo("python");
        assertThat(popularSearches.get(1).getKeyword()).isEqualTo("java");
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