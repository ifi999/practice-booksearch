package com.example.booksearch.config;

import com.example.booksearch.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("DataLoader 테스트")
class DataLoaderTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestDataLoader dataLoader;

    @Test
    @DisplayName("샘플 데이터 로딩 후 최소 5개 도서 존재")
    void loadSampleDataShouldCreateAtLeast5Books() {
        // given
        bookRepository.deleteAll();
        long initialCount = bookRepository.count();
        assertThat(initialCount).isEqualTo(0);

        // when
        dataLoader.loadSampleData();

        // then
        long finalCount = bookRepository.count();
        assertThat(finalCount).isGreaterThanOrEqualTo(5);
    }

    @Test
    @DisplayName("중복 로딩 시 데이터 중복 생성되지 않음")
    void loadSampleDataShouldNotCreateDuplicates() {
        // given
        bookRepository.deleteAll();
        dataLoader.loadSampleData();
        long countAfterFirstLoad = bookRepository.count();

        // when
        dataLoader.loadSampleData();

        // then
        long countAfterSecondLoad = bookRepository.count();
        assertThat(countAfterSecondLoad).isEqualTo(countAfterFirstLoad);
    }

    @Test
    @DisplayName("로딩된 데이터는 모두 유효한 ISBN을 가짐")
    void loadedBooksShouldHaveValidIsbn() {
        // given
        bookRepository.deleteAll();

        // when
        dataLoader.loadSampleData();

        // then
        bookRepository.findAll().forEach(book -> {
            assertThat(book.getIsbn()).isNotNull();
            assertThat(book.getIsbn()).isNotBlank();
            assertThat(book.getIsbn().length()).isBetween(10, 13);
            assertThat(book.getTitle()).isNotNull();
            assertThat(book.getTitle()).isNotBlank();
            assertThat(book.getAuthor()).isNotNull();
            assertThat(book.getAuthor()).isNotBlank();
        });
    }

    @Test
    @DisplayName("로딩된 데이터는 다양한 장르를 포함함")
    void loadedBooksShouldIncludeVariousGenres() {
        // given
        bookRepository.deleteAll();

        // when
        dataLoader.loadSampleData();

        // then
        long totalBooks = bookRepository.count();
        assertThat(totalBooks).isGreaterThanOrEqualTo(5);

        // 다양한 키워드로 검색하여 장르 다양성 확인
        assertThat(bookRepository.findByTitleContaining("Java")).isNotEmpty();
        assertThat(bookRepository.findByTitleContaining("Spring")).isNotEmpty();
        assertThat(bookRepository.findByTitleContaining("파이썬")).isNotEmpty();
        assertThat(bookRepository.findByAuthorContaining("마틴")).isNotEmpty();
    }

    @Test
    @DisplayName("ISBN으로 중복 체크가 정상 작동함")
    void shouldDetectDuplicateIsbn() {
        // given
        bookRepository.deleteAll();
        dataLoader.loadSampleData();

        // when
        String existingIsbn = bookRepository.findAll().get(0).getIsbn();
        boolean isDuplicate = bookRepository.existsByIsbn(existingIsbn);

        // then
        assertThat(isDuplicate).isTrue();
    }
}