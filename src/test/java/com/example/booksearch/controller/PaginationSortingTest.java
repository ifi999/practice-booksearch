package com.example.booksearch.controller;

import com.example.booksearch.config.DataLoader;
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
@DisplayName("페이징 및 정렬 기능 테스트")
class PaginationSortingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
    }

    @Test
    @DisplayName("페이지 크기 설정 테스트")
    void testPageSize() throws Exception {
        // Given
        createTestBooks();

        // When & Then
        mockMvc.perform(get("/api/books")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    @DisplayName("페이지 번호 설정 테스트")
    void testPageNumber() throws Exception {
        // Given
        createTestBooks();

        // When & Then
        mockMvc.perform(get("/api/books")
                        .param("page", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    @DisplayName("제목 오름차순 정렬 테스트")
    void testSortByTitleAsc() throws Exception {
        // Given
        createTestBooks();

        // When & Then
        mockMvc.perform(get("/api/books")
                        .param("sort", "title,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Advanced Java"))
                .andExpect(jsonPath("$.content[1].title").value("Database Design"));
    }

    @Test
    @DisplayName("저자 내림차순 정렬 테스트")
    void testSortByAuthorDesc() throws Exception {
        // Given
        createTestBooks();

        // When & Then
        mockMvc.perform(get("/api/books")
                        .param("sort", "author,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].author").exists());
    }

    @Test
    @DisplayName("출간일 내림차순 정렬 테스트")
    void testSortByPublicationDateDesc() throws Exception {
        // Given
        createTestBooks();

        // When & Then
        mockMvc.perform(get("/api/books")
                        .param("sort", "publicationDate,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].publicationDate").exists());
    }

    @Test
    @DisplayName("다중 정렬 조건 테스트")
    void testMultipleSortCriteria() throws Exception {
        // Given
        createTestBooks();

        // When & Then
        mockMvc.perform(get("/api/books")
                        .param("sort", "author,asc")
                        .param("sort", "title,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("검색과 정렬 조합 테스트")
    void testSearchWithSorting() throws Exception {
        // Given
        createTestBooks();

        // When & Then
        mockMvc.perform(get("/api/search/books")
                        .param("q", "Java")
                        .param("sort", "title,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    private void createTestBooks() {
        Book book1 = Book.builder()
                .isbn("1234567890123")
                .title("Java Basics")
                .author("John Doe")
                .publisher("Tech Press")
                .publicationDate(LocalDate.of(2020, 1, 1))
                .build();

        Book book2 = Book.builder()
                .isbn("1234567890124")
                .title("Advanced Java")
                .author("Jane Smith")
                .publisher("Pro Books")
                .publicationDate(LocalDate.of(2021, 6, 15))
                .build();

        Book book3 = Book.builder()
                .isbn("1234567890125")
                .title("Python Guide")
                .author("Bob Wilson")
                .publisher("Code House")
                .publicationDate(LocalDate.of(2022, 3, 10))
                .build();

        Book book4 = Book.builder()
                .isbn("1234567890126")
                .title("Spring Framework")
                .author("Alice Brown")
                .publisher("Spring Books")
                .publicationDate(LocalDate.of(2023, 9, 1))
                .build();

        Book book5 = Book.builder()
                .isbn("1234567890127")
                .title("Database Design")
                .author("Charlie Davis")
                .publisher("Data Press")
                .publicationDate(LocalDate.of(2019, 12, 5))
                .build();

        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);
        bookRepository.save(book4);
        bookRepository.save(book5);
    }
}