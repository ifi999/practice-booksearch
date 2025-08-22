package com.example.booksearch.service;

import com.example.booksearch.domain.Book;
import com.example.booksearch.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("검색 전략 테스트")
class SearchStrategyTest {

    @Mock
    private BookRepository bookRepository;

    private SearchStrategyManager searchStrategyManager;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        searchStrategyManager = new SearchStrategyManager(bookRepository);
        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("단일 키워드 검색 전략 테스트")
    void testSingleTermSearchStrategy() {
        // Given
        SearchQuery searchQuery = new SearchQuery(Arrays.asList("java"), Collections.emptyList());
        Page<Book> mockResult = new PageImpl<>(Collections.emptyList());
        when(bookRepository.findByTitleContainingOrAuthorContaining(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(mockResult);

        // When
        Page<Book> result = searchStrategyManager.search(searchQuery, pageable);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("OR 연산자 검색 전략 테스트")
    void testOrSearchStrategy() {
        // Given
        SearchQuery searchQuery = new SearchQuery(Arrays.asList("java", "python"), Collections.emptyList());
        Page<Book> mockResult = new PageImpl<>(Collections.emptyList());
        when(bookRepository.findByIncludeTermsOr(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(mockResult);

        // When
        Page<Book> result = searchStrategyManager.search(searchQuery, pageable);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("NOT 연산자 검색 전략 테스트")
    void testNotSearchStrategy() {
        // Given
        SearchQuery searchQuery = new SearchQuery(Collections.emptyList(), Arrays.asList("beginner"));
        Page<Book> mockResult = new PageImpl<>(Collections.emptyList());
        when(bookRepository.findByExcludeTerm(anyString(), any(Pageable.class)))
                .thenReturn(mockResult);

        // When
        Page<Book> result = searchStrategyManager.search(searchQuery, pageable);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("복합 검색 전략 테스트 - 단일 키워드 + NOT")
    void testComplexSearchStrategySingleTerm() {
        // Given
        SearchQuery searchQuery = new SearchQuery(Arrays.asList("java"), Arrays.asList("beginner"));
        Page<Book> mockResult = new PageImpl<>(Collections.emptyList());
        when(bookRepository.findByIncludeTermAndExclude(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(mockResult);

        // When
        Page<Book> result = searchStrategyManager.search(searchQuery, pageable);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("복합 검색 전략 테스트 - OR + NOT")
    void testComplexSearchStrategyOrWithNot() {
        // Given
        SearchQuery searchQuery = new SearchQuery(Arrays.asList("java", "python"), Arrays.asList("beginner"));
        Page<Book> mockResult = new PageImpl<>(Collections.emptyList());
        when(bookRepository.findByIncludeTermsOrAndExclude(anyString(), anyString(), anyString(), any(Pageable.class)))
                .thenReturn(mockResult);

        // When
        Page<Book> result = searchStrategyManager.search(searchQuery, pageable);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("빈 쿼리 검색 전략 테스트")
    void testEmptySearchStrategy() {
        // Given
        SearchQuery searchQuery = new SearchQuery(Collections.emptyList(), Collections.emptyList());
        Page<Book> mockResult = new PageImpl<>(Collections.emptyList());
        when(bookRepository.findAll(any(Pageable.class)))
                .thenReturn(mockResult);

        // When
        Page<Book> result = searchStrategyManager.search(searchQuery, pageable);

        // Then
        assertThat(result).isNotNull();
    }
}