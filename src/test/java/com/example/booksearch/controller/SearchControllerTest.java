package com.example.booksearch.controller;

import com.example.booksearch.config.TestDataLoader;
import com.example.booksearch.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("SearchController 통합 테스트")
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestDataLoader dataLoader;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        // Clear existing data first
        bookRepository.deleteAll();
        // Load test data
        dataLoader.loadSampleData();
        // Ensure data is available
        long bookCount = bookRepository.count();
        if (bookCount < 5) {
            throw new IllegalStateException("Test data not loaded properly. Expected at least 5 books, found: " + bookCount);
        }
    }

    @Test
    @DisplayName("도서 검색 - 기본 키워드 검색")
    void searchBooksByKeyword() throws Exception {
        mockMvc.perform(get("/api/search/books")
                        .param("q", "Java")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.content[0].title").exists())
                .andExpect(jsonPath("$.content[0].author").exists())
                .andExpect(jsonPath("$.content[0].isbn").exists());
    }

    @Test
    @DisplayName("도서 검색 - 제목으로 검색")
    void searchBooksByTitle() throws Exception {
        mockMvc.perform(get("/api/search/books")
                        .param("q", "Clean")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].title", containsString("Clean")));
    }

    @Test
    @DisplayName("도서 검색 - 저자로 검색")
    void searchBooksByAuthor() throws Exception {
        mockMvc.perform(get("/api/search/books")
                        .param("q", "마틴")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].author", containsString("마틴")));
    }

    @Test
    @DisplayName("도서 검색 - 검색 결과 없음")
    void searchBooksNoResults() throws Exception {
        mockMvc.perform(get("/api/search/books")
                        .param("q", "NoResultKeyword")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));
    }

    @Test
    @DisplayName("도서 검색 - 페이징 처리")
    void searchBooksWithPaging() throws Exception {
        mockMvc.perform(get("/api/search/books")
                        .param("q", "Java")
                        .param("page", "0")
                        .param("size", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.size", is(1)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.totalElements", greaterThan(0)));
    }

    @Test
    @DisplayName("도서 검색 - 빈 쿼리 파라미터")
    void searchBooksWithEmptyQuery() throws Exception {
        mockMvc.perform(get("/api/search/books")
                        .param("q", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements", greaterThan(0)));
    }

    @Test
    @DisplayName("도서 검색 - 쿼리 파라미터 없음")
    void searchBooksWithoutQuery() throws Exception {
        mockMvc.perform(get("/api/search/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements", greaterThan(0)));
    }

    @Test
    @DisplayName("도서 검색 - 정확한 키워드 대소문자 매칭")
    void searchBooksExactCase() throws Exception {
        // "Java"로 검색 (정확한 케이스)
        mockMvc.perform(get("/api/search/books")
                        .param("q", "Java")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("도서 검색 - 기본 페이징 설정")
    void searchBooksWithDefaultPaging() throws Exception {
        mockMvc.perform(get("/api/search/books")
                        .param("q", "Java")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size", is(20)))
                .andExpect(jsonPath("$.number", is(0)));
    }

    @Test
    @DisplayName("도서 검색 - 잘못된 페이지 번호")
    void searchBooksWithInvalidPageNumber() throws Exception {
        mockMvc.perform(get("/api/search/books")
                        .param("q", "Java")
                        .param("page", "999")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }
}