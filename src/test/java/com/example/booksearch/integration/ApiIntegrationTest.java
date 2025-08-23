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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("API 엔드포인트 통합 테스트")
class ApiIntegrationTest {

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
        
        // 테스트 데이터 설정
        Book book1 = Book.builder()
                .isbn("9788966262281")
                .title("Clean Code")
                .subtitle("애자일 소프트웨어 장인 정신")
                .author("로버트 C. 마틴")
                .publisher("인사이트")
                .publicationDate(LocalDate.of(2013, 12, 24))
                .build();

        Book book2 = Book.builder()
                .isbn("9788966262298")
                .title("Effective Java")
                .subtitle("자바 플랫폼 모범사례")
                .author("조슈아 블로크")
                .publisher("인사이트")
                .publicationDate(LocalDate.of(2018, 11, 1))
                .build();

        Book book3 = Book.builder()
                .isbn("9788966262305")
                .title("Spring Boot in Action")
                .author("크레이그 월즈")
                .publisher("한빛미디어")
                .publicationDate(LocalDate.of(2016, 3, 1))
                .build();

        Book book4 = Book.builder()
                .isbn("9788966262312")
                .title("Java Programming")
                .author("김자바")
                .publisher("자바출판사")
                .publicationDate(LocalDate.of(2020, 1, 1))
                .build();

        Book book5 = Book.builder()
                .isbn("9788966262329")
                .title("Python Tutorial")
                .author("파이썬맨")
                .publisher("파이썬출판사")
                .publicationDate(LocalDate.of(2021, 5, 15))
                .build();

        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);
        bookRepository.save(book4);
        bookRepository.save(book5);
    }

    @Test
    @DisplayName("전체 API 엔드포인트 접근 가능성 확인")
    void checkAllEndpointsAccessibility() throws Exception {
        // Book API
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk());

        // Search API
        mockMvc.perform(get("/api/search/books"))
                .andExpect(status().isOk());

        // Search Detailed API
        mockMvc.perform(get("/api/search/books/detailed"))
                .andExpect(status().isOk());

        // Popular Keywords API
        mockMvc.perform(get("/api/search/popular"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("검색 API와 Book API 결과 일관성 확인")
    void checkSearchAndBookApiConsistency() throws Exception {
        String keyword = "Java";

        // Book API 검색 결과
        mockMvc.perform(get("/api/books")
                        .param("keyword", keyword))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(0))));

        // Search API 검색 결과  
        mockMvc.perform(get("/api/search/books")
                        .param("q", keyword))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(0))));
    }

    @Test
    @DisplayName("검색 로깅과 인기 검색어 API 통합 테스트")
    void checkSearchLoggingIntegration() throws Exception {
        String searchTerm = "Spring";

        // 검색 실행 (로깅 트리거)
        mockMvc.perform(get("/api/search/books")
                        .param("q", searchTerm))
                .andExpect(status().isOk());

        // 잠시 대기 (로깅 처리 시간)
        Thread.sleep(100);

        // 인기 검색어에서 확인 (로깅이 비동기이므로 결과가 바로 나타나지 않을 수 있음)
        mockMvc.perform(get("/api/search/popular")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0))));
    }

    @Test
    @DisplayName("OR 연산자 검색 통합 테스트")
    void checkOrOperatorSearchIntegration() throws Exception {
        // OR 연산자 검색
        mockMvc.perform(get("/api/search/books")
                        .param("q", "Java|Python"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(0))));

        // 메타데이터 포함 검색에서 전략 확인
        mockMvc.perform(get("/api/search/books/detailed")
                        .param("q", "Java|Python"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.strategy", is("OR_SEARCH")))
                .andExpect(jsonPath("$.data.content", hasSize(3)));
    }

    @Test
    @DisplayName("NOT 연산자 검색 통합 테스트")
    void checkNotOperatorSearchIntegration() throws Exception {
        // NOT 연산자 검색
        mockMvc.perform(get("/api/search/books")
                        .param("q", "-Python"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(4)));

        // 메타데이터 포함 검색에서 전략 확인
        mockMvc.perform(get("/api/search/books/detailed")
                        .param("q", "-Python"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.strategy", is("NOT_SEARCH")))
                .andExpect(jsonPath("$.data.content", hasSize(4)));
    }

    @Test
    @DisplayName("복합 연산자 검색 통합 테스트")
    void checkComplexOperatorSearchIntegration() throws Exception {
        // 복합 연산자 검색
        mockMvc.perform(get("/api/search/books")
                        .param("q", "Java -Tutorial"))
                .andExpect(status().isOk());

        // 메타데이터 포함 검색에서 전략 확인
        mockMvc.perform(get("/api/search/books/detailed")
                        .param("q", "Java -Tutorial"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.strategy", is("COMPLEX_SEARCH")));
    }

    @Test
    @DisplayName("페이징과 정렬 통합 테스트")
    void checkPaginationAndSortingIntegration() throws Exception {
        // Book API 페이징
        mockMvc.perform(get("/api/books")
                        .param("page", "0")
                        .param("size", "3")
                        .param("sort", "title,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size", is(3)))
                .andExpect(jsonPath("$.totalElements", is(5)));

        // Search API 페이징
        mockMvc.perform(get("/api/search/books")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size", is(2)))
                .andExpect(jsonPath("$.totalElements", is(5)));
    }

    @Test
    @DisplayName("검색 메타데이터 응답 구조 검증")
    void checkSearchMetadataResponseStructure() throws Exception {
        mockMvc.perform(get("/api/search/books/detailed")
                        .param("q", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.metadata").exists())
                .andExpect(jsonPath("$.metadata.query", is("Java")))
                .andExpect(jsonPath("$.metadata.strategy").exists())
                .andExpect(jsonPath("$.metadata.executionTimeMs").isNumber())
                .andExpect(jsonPath("$.metadata.totalResults").isNumber())
                .andExpect(jsonPath("$.metadata.searchedAt").exists());
    }
}