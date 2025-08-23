package com.example.booksearch.integration;

import com.example.booksearch.domain.Book;
import com.example.booksearch.repository.BookRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("에러 응답 통합 테스트")
class ErrorHandlingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        
        Book testBook = Book.builder()
                .isbn("9788966262281")
                .title("Test Book")
                .author("Test Author")
                .publisher("Test Publisher")
                .publicationDate(LocalDate.of(2020, 1, 1))
                .build();

        bookRepository.save(testBook);
    }

    @Test
    @DisplayName("존재하지 않는 책 ID 조회 시 글로벌 예외 처리")
    void testBookNotFoundExceptionHandling() throws Exception {
        mockMvc.perform(get("/api/books/{id}", 99999L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Book not found with id: 99999")))
                .andExpect(jsonPath("$.path", is("/api/books/99999")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("잘못된 ID 형식으로 인한 타입 변환 오류 처리")
    void testMethodArgumentTypeMismatchExceptionHandling() throws Exception {
        mockMvc.perform(get("/api/books/{id}", "invalid-id"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", containsString("Invalid value 'invalid-id'")))
                .andExpect(jsonPath("$.path", is("/api/books/invalid-id")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("지원하지 않는 HTTP 메서드 오류 처리")
    void testHttpRequestMethodNotSupportedExceptionHandling() throws Exception {
        mockMvc.perform(post("/api/books"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(405)))
                .andExpect(jsonPath("$.error", is("Method Not Allowed")))
                .andExpect(jsonPath("$.message", containsString("Request method 'POST' not supported")))
                .andExpect(jsonPath("$.path", is("/api/books")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("지원하지 않는 미디어 타입 오류 처리")
    void testHttpMediaTypeNotAcceptableExceptionHandling() throws Exception {
        mockMvc.perform(get("/api/books")
                        .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @DisplayName("존재하지 않는 엔드포인트 404 처리")
    void testNonExistentEndpoint() throws Exception {
        mockMvc.perform(get("/api/nonexistent"))
                .andExpect(status().isNotFound());
    }
}