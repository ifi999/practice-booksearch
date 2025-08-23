package com.example.booksearch.controller;

import com.example.booksearch.domain.Book;
import com.example.booksearch.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("검색 메타데이터 API 테스트")
class SearchMetadataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        createTestBooks();
    }

    @Test
    @DisplayName("검색 메타데이터 포함 응답 구조 테스트")
    void testSearchWithMetadataResponseStructure() throws Exception {
        mockMvc.perform(get("/api/search/books/detailed")
                        .param("q", "java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.metadata").exists())
                .andExpect(jsonPath("$.metadata.query").value("java"))
                .andExpect(jsonPath("$.metadata.strategy").exists())
                .andExpect(jsonPath("$.metadata.executionTimeMs").exists())
                .andExpect(jsonPath("$.metadata.totalResults").exists())
                .andExpect(jsonPath("$.metadata.searchedAt").exists());
    }

    @Test
    @DisplayName("단일 키워드 검색 전략 메타데이터 테스트")
    void testSingleTermSearchStrategy() throws Exception {
        mockMvc.perform(get("/api/search/books/detailed")
                        .param("q", "java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.strategy").value("SINGLE_TERM_SEARCH"))
                .andExpect(jsonPath("$.metadata.query").value("java"));
    }

    @Test
    @DisplayName("OR 연산자 검색 전략 메타데이터 테스트")
    void testOrSearchStrategy() throws Exception {
        mockMvc.perform(get("/api/search/books/detailed")
                        .param("q", "java|python"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.strategy").value("OR_SEARCH"))
                .andExpect(jsonPath("$.metadata.query").value("java|python"));
    }

    @Test
    @DisplayName("NOT 연산자 검색 전략 메타데이터 테스트")
    void testNotSearchStrategy() throws Exception {
        mockMvc.perform(get("/api/search/books/detailed")
                        .param("q", "-tutorial"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.strategy").value("NOT_SEARCH"))
                .andExpect(jsonPath("$.metadata.query").value("-tutorial"));
    }

    @Test
    @DisplayName("복합 검색 전략 메타데이터 테스트")
    void testComplexSearchStrategy() throws Exception {
        mockMvc.perform(get("/api/search/books/detailed")
                        .param("q", "java -tutorial"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.strategy").value("COMPLEX_SEARCH"))
                .andExpect(jsonPath("$.metadata.query").value("java -tutorial"));
    }

    @Test
    @DisplayName("빈 쿼리 검색 전략 메타데이터 테스트")
    void testEmptySearchStrategy() throws Exception {
        mockMvc.perform(get("/api/search/books/detailed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.strategy").value("ALL_BOOKS"))
                .andExpect(jsonPath("$.metadata.totalResults").value(5));
    }

    @Test
    @DisplayName("검색 실행 시간 측정 테스트")
    void testExecutionTimeMeasurement() throws Exception {
        mockMvc.perform(get("/api/search/books/detailed")
                        .param("q", "java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.executionTimeMs").isNumber())
                .andExpect(jsonPath("$.metadata.executionTimeMs").value(org.hamcrest.Matchers.greaterThanOrEqualTo(0)));
    }

    @Test
    @DisplayName("총 결과 수 정확성 테스트")
    void testTotalResultsAccuracy() throws Exception {
        mockMvc.perform(get("/api/search/books/detailed")
                        .param("q", "programming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.totalResults").isNumber())
                .andExpect(jsonPath("$.data.totalElements").exists());
    }

    private void createTestBooks() {
        Book book1 = Book.builder()
                .isbn("1111111111111")
                .title("Java Programming")
                .author("John Smith")
                .publisher("Tech Books")
                .publicationDate(LocalDate.of(2020, 1, 1))
                .build();

        Book book2 = Book.builder()
                .isbn("2222222222222")
                .title("Python Guide")
                .author("Jane Doe")
                .publisher("Code Press")
                .publicationDate(LocalDate.of(2021, 2, 1))
                .build();

        Book book3 = Book.builder()
                .isbn("3333333333333")
                .title("JavaScript Tutorial")
                .author("Bob Wilson")
                .publisher("Web Books")
                .publicationDate(LocalDate.of(2022, 3, 1))
                .build();

        Book book4 = Book.builder()
                .isbn("4444444444444")
                .title("Advanced Programming")
                .author("Alice Brown")
                .publisher("Pro Books")
                .publicationDate(LocalDate.of(2023, 4, 1))
                .build();

        Book book5 = Book.builder()
                .isbn("5555555555555")
                .title("Database Design")
                .author("Charlie Green")
                .publisher("Data Books")
                .publicationDate(LocalDate.of(2019, 5, 1))
                .build();

        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);
        bookRepository.save(book4);
        bookRepository.save(book5);
    }
}