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
@DisplayName("BookController 통합 테스트")
class BookControllerTest {

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
    @DisplayName("모든 도서 목록 조회 - 기본 페이징")
    void getAllBooksWithDefaultPaging() throws Exception {
        mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].isbn").exists())
                .andExpect(jsonPath("$.content[0].title").exists())
                .andExpect(jsonPath("$.content[0].author").exists())
                .andExpect(jsonPath("$.size", is(20)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.totalElements", is(3)))
                .andExpect(jsonPath("$.totalPages", is(1)));
    }

    @Test
    @DisplayName("도서 목록 조회 - 커스텀 페이징")
    void getAllBooksWithCustomPaging() throws Exception {
        mockMvc.perform(get("/api/books")
                        .param("page", "0")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.size", is(2)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.totalElements", is(3)))
                .andExpect(jsonPath("$.totalPages", is(2)));
    }

    @Test
    @DisplayName("특정 도서 조회 - ID로 조회")
    void getBookById() throws Exception {
        Book savedBook = bookRepository.findByIsbn("9788966262281").orElseThrow();
        Long bookId = savedBook.getId();

        mockMvc.perform(get("/api/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn", is("9788966262281")))
                .andExpect(jsonPath("$.title", is("Clean Code")))
                .andExpect(jsonPath("$.subtitle", is("애자일 소프트웨어 장인 정신")))
                .andExpect(jsonPath("$.author", is("로버트 C. 마틴")))
                .andExpect(jsonPath("$.publisher", is("인사이트")))
                .andExpect(jsonPath("$.publicationDate", is("2013-12-24")));
    }

    @Test
    @DisplayName("존재하지 않는 도서 조회 - 404 응답")
    void getBookByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/books/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("제목으로 도서 검색 - 키워드 포함")
    void searchBooksByTitle() throws Exception {
        mockMvc.perform(get("/api/books")
                        .param("keyword", "Java")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", is("Effective Java")));
    }

    @Test
    @DisplayName("저자로 도서 검색 - 키워드 포함")
    void searchBooksByAuthor() throws Exception {
        mockMvc.perform(get("/api/books")
                        .param("keyword", "마틴")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].author", is("로버트 C. 마틴")));
    }

    @Test
    @DisplayName("키워드 검색 결과 없음 - 빈 목록 반환")
    void searchBooksNoResults() throws Exception {
        mockMvc.perform(get("/api/books")
                        .param("keyword", "Python")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));
    }

    @Test
    @DisplayName("키워드 검색과 페이징 조합")
    void searchBooksWithPaging() throws Exception {
        // "블로크" 저자 검색 (1개 결과 예상)  
        mockMvc.perform(get("/api/books")
                        .param("keyword", "블로크")
                        .param("page", "0")
                        .param("size", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.size", is(1)))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.totalPages", is(1)));
    }

    @Test
    @DisplayName("잘못된 페이지 번호 요청 - 빈 결과 반환")
    void getInvalidPageNumber() throws Exception {
        mockMvc.perform(get("/api/books")
                        .param("page", "10")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }
}