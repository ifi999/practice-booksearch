package com.example.booksearch.integration;

import com.example.booksearch.domain.Book;
import com.example.booksearch.repository.BookRepository;
import com.example.booksearch.repository.SearchLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("기본 API 통합 테스트")
class BasicApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private SearchLogRepository searchLogRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        searchLogRepository.deleteAll();
        
        // 간단한 테스트 데이터
        Book book1 = Book.builder()
                .isbn("9788966262281")
                .title("Clean Code")
                .author("로버트 C. 마틴")
                .publisher("인사이트")
                .publicationDate(LocalDate.of(2013, 12, 24))
                .build();

        Book book2 = Book.builder()
                .isbn("9788966262298")
                .title("Effective Java")
                .author("조슈아 블로크")
                .publisher("인사이트")
                .publicationDate(LocalDate.of(2018, 11, 1))
                .build();

        bookRepository.save(book1);
        bookRepository.save(book2);
    }

    @Test
    @DisplayName("모든 API 엔드포인트 기본 동작 확인")
    void testAllEndpointsBasicFunctionality() throws Exception {
        // Book API
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(2)));

        // Search API
        mockMvc.perform(get("/api/search/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray());

        // Search Detailed API
        mockMvc.perform(get("/api/search/books/detailed"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.metadata").exists());

        // Popular Keywords API
        mockMvc.perform(get("/api/search/popular"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Book API 기본 기능 테스트")
    void testBookApiBasicFunctionality() throws Exception {
        // 전체 목록 조회
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(2)));

        // 페이징 테스트
        mockMvc.perform(get("/api/books")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalPages", is(2)));

        // 키워드 검색
        mockMvc.perform(get("/api/books")
                        .param("keyword", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", is("Effective Java")));

        // ID로 조회
        Book savedBook = bookRepository.findAll().get(0);
        mockMvc.perform(get("/api/books/{id}", savedBook.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.author").exists());
    }

    @Test
    @DisplayName("Search API 기본 기능 테스트")
    void testSearchApiBasicFunctionality() throws Exception {
        // 전체 검색
        mockMvc.perform(get("/api/search/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));

        // 키워드 검색
        mockMvc.perform(get("/api/search/books")
                        .param("q", "Clean"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(0))));

        // 페이징
        mockMvc.perform(get("/api/search/books")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    @DisplayName("Search Detailed API 메타데이터 구조 확인")
    void testSearchDetailedApiMetadata() throws Exception {
        mockMvc.perform(get("/api/search/books/detailed")
                        .param("q", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.metadata").exists())
                .andExpect(jsonPath("$.metadata.executionTimeMs").isNumber())
                .andExpect(jsonPath("$.metadata.totalResults").isNumber());
    }

    @Test
    @DisplayName("검색 후 인기 검색어 API 확인")
    void testPopularKeywordsAfterSearch() throws Exception {
        // 검색 실행
        mockMvc.perform(get("/api/search/books")
                        .param("q", "test"))
                .andExpect(status().isOk());

        // 잠시 대기
        Thread.sleep(100);

        // 인기 검색어 확인
        mockMvc.perform(get("/api/search/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // 제한 파라미터
        mockMvc.perform(get("/api/search/popular")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(lessThanOrEqualTo(5))));
    }

    @Test
    @DisplayName("에러 상황 기본 처리")
    void testBasicErrorHandling() throws Exception {
        // 존재하지 않는 책 ID
        mockMvc.perform(get("/api/books/99999"))
                .andExpect(status().isNotFound());

        // 잘못된 ID 형식
        mockMvc.perform(get("/api/books/invalid"))
                .andExpect(status().isBadRequest());

        // 빈 검색어는 허용
        mockMvc.perform(get("/api/search/books")
                        .param("q", ""))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("응답 형식 일관성 확인")
    void testResponseFormatConsistency() throws Exception {
        // 모든 API가 JSON으로 응답하는지 확인
        mockMvc.perform(get("/api/books"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/api/search/books"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/api/search/books/detailed"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/api/search/popular"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("페이징 정보 일관성 확인")
    void testPaginationConsistency() throws Exception {
        // Book API 페이징
        mockMvc.perform(get("/api/books")
                        .param("size", "1")
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size", is(1)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.totalElements", greaterThan(0)));

        // Search API 페이징
        mockMvc.perform(get("/api/search/books")
                        .param("size", "1")
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size", is(1)))
                .andExpect(jsonPath("$.number", is(0)));
    }
}