package com.example.booksearch.repository;

import com.example.booksearch.domain.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("BookRepository 테스트")
class BookRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookRepository bookRepository;

    private Book sampleBook;

    @BeforeEach
    void setUp() {
        sampleBook = Book.builder()
                .isbn("9788966262281")
                .title("Clean Code")
                .subtitle("애자일 소프트웨어 장인 정신")
                .author("로버트 C. 마틴")
                .publisher("인사이트")
                .publicationDate(LocalDate.of(2013, 12, 24))
                .build();
    }

    @Test
    @DisplayName("Book 저장 및 조회 테스트")
    void saveAndFindById() {
        // when
        Book savedBook = bookRepository.save(sampleBook);
        Optional<Book> foundBook = bookRepository.findById(savedBook.getId());

        // then
        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getIsbn()).isEqualTo("9788966262281");
        assertThat(foundBook.get().getTitle()).isEqualTo("Clean Code");
        assertThat(foundBook.get().getId()).isNotNull();
        assertThat(foundBook.get().getCreatedAt()).isNotNull();
        assertThat(foundBook.get().getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("ISBN으로 Book 조회 테스트")
    void findByIsbn() {
        // given
        bookRepository.save(sampleBook);

        // when
        Optional<Book> foundBook = bookRepository.findByIsbn("9788966262281");

        // then
        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getTitle()).isEqualTo("Clean Code");
        assertThat(foundBook.get().getAuthor()).isEqualTo("로버트 C. 마틴");
    }

    @Test
    @DisplayName("존재하지 않는 ISBN으로 조회 시 빈 Optional 반환")
    void findByIsbnNotFound() {
        // when
        Optional<Book> foundBook = bookRepository.findByIsbn("0000000000000");

        // then
        assertThat(foundBook).isEmpty();
    }

    @Test
    @DisplayName("제목에 특정 키워드가 포함된 Book 목록 조회")
    void findByTitleContaining() {
        // given
        Book book1 = Book.builder()
                .isbn("1111111111111")
                .title("Java Programming")
                .author("Author 1")
                .build();
        
        Book book2 = Book.builder()
                .isbn("2222222222222")
                .title("Advanced Java")
                .author("Author 2")
                .build();

        Book book3 = Book.builder()
                .isbn("3333333333333")
                .title("Python Guide")
                .author("Author 3")
                .build();

        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);

        // when
        List<Book> javaBooks = bookRepository.findByTitleContaining("Java");

        // then
        assertThat(javaBooks).hasSize(2);
        assertThat(javaBooks).extracting(Book::getTitle)
                .containsExactlyInAnyOrder("Java Programming", "Advanced Java");
    }

    @Test
    @DisplayName("저자명에 특정 키워드가 포함된 Book 목록 조회")
    void findByAuthorContaining() {
        // given
        Book book1 = Book.builder()
                .isbn("1111111111111")
                .title("Book 1")
                .author("김철수")
                .build();

        Book book2 = Book.builder()
                .isbn("2222222222222")
                .title("Book 2")
                .author("이철수")
                .build();

        Book book3 = Book.builder()
                .isbn("3333333333333")
                .title("Book 3")
                .author("박영희")
                .build();

        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);

        // when
        List<Book> books = bookRepository.findByAuthorContaining("철수");

        // then
        assertThat(books).hasSize(2);
        assertThat(books).extracting(Book::getAuthor)
                .containsExactlyInAnyOrder("김철수", "이철수");
    }

    @Test
    @DisplayName("제목 또는 저자명에 키워드가 포함된 Book 목록 조회")
    void findByTitleContainingOrAuthorContaining() {
        // given
        Book book1 = Book.builder()
                .isbn("1111111111111")
                .title("Spring Boot Guide")
                .author("김개발")
                .build();

        Book book2 = Book.builder()
                .isbn("2222222222222")
                .title("Java Programming")
                .author("Spring Master")
                .build();

        Book book3 = Book.builder()
                .isbn("3333333333333")
                .title("Python Guide")
                .author("파이썬 전문가")
                .build();

        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);

        // when
        List<Book> springBooks = bookRepository.findByTitleContainingOrAuthorContaining("Spring", "Spring");

        // then
        assertThat(springBooks).hasSize(2);
        assertThat(springBooks).extracting(Book::getIsbn)
                .containsExactlyInAnyOrder("1111111111111", "2222222222222");
    }

    @Test
    @DisplayName("모든 Book 개수 조회")
    void count() {
        // given
        Book book1 = Book.builder().isbn("1111111111111").title("Book 1").author("Author 1").build();
        Book book2 = Book.builder().isbn("2222222222222").title("Book 2").author("Author 2").build();
        Book book3 = Book.builder().isbn("3333333333333").title("Book 3").author("Author 3").build();

        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);

        // when
        long count = bookRepository.count();

        // then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("ISBN으로 Book 존재 여부 확인")
    void existsByIsbn() {
        // given
        bookRepository.save(sampleBook);

        // when & then
        assertThat(bookRepository.existsByIsbn("9788966262281")).isTrue();
        assertThat(bookRepository.existsByIsbn("0000000000000")).isFalse();
    }
}