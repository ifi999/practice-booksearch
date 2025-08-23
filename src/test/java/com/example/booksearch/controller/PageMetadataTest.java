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
@DisplayName("페이지 메타데이터 테스트")
class PageMetadataTest {

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
    @DisplayName("페이지 메타데이터 포함 확인")
    void testPageMetadata() throws Exception {
        mockMvc.perform(get("/api/books")
                        .param("page", "0")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(7))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(3))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false))
                .andExpect(jsonPath("$.numberOfElements").value(3))
                .andExpect(jsonPath("$.empty").value(false));
    }

    @Test
    @DisplayName("빈 페이지 메타데이터 확인")
    void testEmptyPageMetadata() throws Exception {
        bookRepository.deleteAll();

        mockMvc.perform(get("/api/books")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.numberOfElements").value(0))
                .andExpect(jsonPath("$.empty").value(true));
    }

    @Test
    @DisplayName("마지막 페이지 메타데이터 확인")
    void testLastPageMetadata() throws Exception {
        mockMvc.perform(get("/api/books")
                        .param("page", "2")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(7))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.number").value(2))
                .andExpect(jsonPath("$.size").value(3))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.numberOfElements").value(1));
    }

    @Test
    @DisplayName("검색 결과 페이지 메타데이터 확인")
    void testSearchPageMetadata() throws Exception {
        mockMvc.perform(get("/api/search/books")
                        .param("q", "Java")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(2));
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
                .title("Advanced Java")
                .author("Jane Doe")
                .publisher("Pro Books")
                .publicationDate(LocalDate.of(2021, 2, 1))
                .build();

        Book book3 = Book.builder()
                .isbn("3333333333333")
                .title("Python Guide")
                .author("Bob Wilson")
                .publisher("Code Press")
                .publicationDate(LocalDate.of(2022, 3, 1))
                .build();

        Book book4 = Book.builder()
                .isbn("4444444444444")
                .title("Spring Boot")
                .author("Alice Brown")
                .publisher("Framework Books")
                .publicationDate(LocalDate.of(2023, 4, 1))
                .build();

        Book book5 = Book.builder()
                .isbn("5555555555555")
                .title("Database Design")
                .author("Charlie Green")
                .publisher("Data Books")
                .publicationDate(LocalDate.of(2019, 5, 1))
                .build();

        Book book6 = Book.builder()
                .isbn("6666666666666")
                .title("React Development")
                .author("David White")
                .publisher("Frontend Press")
                .publicationDate(LocalDate.of(2021, 6, 1))
                .build();

        Book book7 = Book.builder()
                .isbn("7777777777777")
                .title("Node.js Backend")
                .author("Emma Black")
                .publisher("Backend Books")
                .publicationDate(LocalDate.of(2022, 7, 1))
                .build();

        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);
        bookRepository.save(book4);
        bookRepository.save(book5);
        bookRepository.save(book6);
        bookRepository.save(book7);
    }
}