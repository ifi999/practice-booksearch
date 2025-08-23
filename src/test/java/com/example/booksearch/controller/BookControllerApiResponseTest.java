package com.example.booksearch.controller;

import com.example.booksearch.domain.Book;
import com.example.booksearch.repository.BookRepository;
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
@DisplayName("BookController ApiResponse 통합 테스트")
class BookControllerApiResponseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        
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

        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);
    }

    @Test
    @DisplayName("모든 도서 목록 조회 - ApiResponse 구조 확인")
    void getAllBooksWithApiResponse() throws Exception {
        mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("요청이 성공적으로 처리되었습니다.")))
                .andExpect(jsonPath("$.data.content", hasSize(3)))
                .andExpect(jsonPath("$.data.content[0].isbn").exists())
                .andExpect(jsonPath("$.data.content[0].title").exists())
                .andExpect(jsonPath("$.data.content[0].author").exists())
                .andExpect(jsonPath("$.data.size", is(20)))
                .andExpect(jsonPath("$.data.number", is(0)))
                .andExpect(jsonPath("$.data.totalElements", is(3)))
                .andExpect(jsonPath("$.data.totalPages", is(1)))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("특정 도서 조회 - ApiResponse 구조 확인")
    void getBookByIdWithApiResponse() throws Exception {
        Book savedBook = bookRepository.findByIsbn("9788966262281").orElseThrow();
        Long bookId = savedBook.getId();

        mockMvc.perform(get("/api/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("요청이 성공적으로 처리되었습니다.")))
                .andExpect(jsonPath("$.data.isbn", is("9788966262281")))
                .andExpect(jsonPath("$.data.title", is("Clean Code")))
                .andExpect(jsonPath("$.data.subtitle", is("애자일 소프트웨어 장인 정신")))
                .andExpect(jsonPath("$.data.author", is("로버트 C. 마틴")))
                .andExpect(jsonPath("$.data.publisher", is("인사이트")))
                .andExpect(jsonPath("$.data.publicationDate", is("2013-12-24")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("제목으로 도서 검색 - ApiResponse 구조 확인")
    void searchBooksByTitleWithApiResponse() throws Exception {
        mockMvc.perform(get("/api/books")
                        .param("keyword", "Java")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].title", is("Effective Java")))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}