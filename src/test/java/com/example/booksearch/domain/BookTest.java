package com.example.booksearch.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Book 엔티티 테스트")
class BookTest {

    @Test
    @DisplayName("Book 엔티티 생성 테스트")
    void createBook() {
        // given
        String isbn = "9788966262281";
        String title = "Clean Code";
        String subtitle = "애자일 소프트웨어 장인 정신";
        String author = "로버트 C. 마틴";
        String publisher = "인사이트";
        LocalDate publicationDate = LocalDate.of(2013, 12, 24);

        // when
        Book book = Book.builder()
                .isbn(isbn)
                .title(title)
                .subtitle(subtitle)
                .author(author)
                .publisher(publisher)
                .publicationDate(publicationDate)
                .build();

        // then
        assertThat(book.getIsbn()).isEqualTo(isbn);
        assertThat(book.getTitle()).isEqualTo(title);
        assertThat(book.getSubtitle()).isEqualTo(subtitle);
        assertThat(book.getAuthor()).isEqualTo(author);
        assertThat(book.getPublisher()).isEqualTo(publisher);
        assertThat(book.getPublicationDate()).isEqualTo(publicationDate);
    }

    @Test
    @DisplayName("필수 필드만으로 Book 생성 테스트")
    void createBookWithRequiredFieldsOnly() {
        // given
        String isbn = "9788966262281";
        String title = "Clean Code";
        String author = "로버트 C. 마틴";

        // when
        Book book = Book.builder()
                .isbn(isbn)
                .title(title)
                .author(author)
                .build();

        // then
        assertThat(book.getIsbn()).isEqualTo(isbn);
        assertThat(book.getTitle()).isEqualTo(title);
        assertThat(book.getAuthor()).isEqualTo(author);
        assertThat(book.getSubtitle()).isNull();
        assertThat(book.getPublisher()).isNull();
        assertThat(book.getPublicationDate()).isNull();
    }

    @Test
    @DisplayName("ISBN이 null인 경우 예외 발생")
    void createBookWithNullIsbn() {
        assertThatThrownBy(() -> Book.builder()
                .title("Clean Code")
                .author("로버트 C. 마틴")
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ISBN은 필수 입력값입니다.");
    }

    @Test
    @DisplayName("ISBN이 빈 문자열인 경우 예외 발생")
    void createBookWithEmptyIsbn() {
        assertThatThrownBy(() -> Book.builder()
                .isbn("  ")
                .title("Clean Code")
                .author("로버트 C. 마틴")
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ISBN은 필수 입력값입니다.");
    }

    @Test
    @DisplayName("제목이 null인 경우 예외 발생")
    void createBookWithNullTitle() {
        assertThatThrownBy(() -> Book.builder()
                .isbn("9788966262281")
                .author("로버트 C. 마틴")
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("제목은 필수 입력값입니다.");
    }

    @Test
    @DisplayName("제목이 빈 문자열인 경우 예외 발생")
    void createBookWithEmptyTitle() {
        assertThatThrownBy(() -> Book.builder()
                .isbn("9788966262281")
                .title("  ")
                .author("로버트 C. 마틴")
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("제목은 필수 입력값입니다.");
    }

    @Test
    @DisplayName("저자가 null인 경우 예외 발생")
    void createBookWithNullAuthor() {
        assertThatThrownBy(() -> Book.builder()
                .isbn("9788966262281")
                .title("Clean Code")
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("저자는 필수 입력값입니다.");
    }

    @Test
    @DisplayName("저자가 빈 문자열인 경우 예외 발생")
    void createBookWithEmptyAuthor() {
        assertThatThrownBy(() -> Book.builder()
                .isbn("9788966262281")
                .title("Clean Code")
                .author("  ")
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("저자는 필수 입력값입니다.");
    }

    @Test
    @DisplayName("Book 엔티티 동등성 테스트 - ISBN 기반")
    void bookEquality() {
        // given
        String isbn = "9788966262281";
        Book book1 = Book.builder()
                .isbn(isbn)
                .title("Clean Code")
                .author("로버트 C. 마틴")
                .build();

        Book book2 = Book.builder()
                .isbn(isbn)
                .title("다른 제목")
                .author("다른 저자")
                .build();

        // when & then
        assertThat(book1).isEqualTo(book2);
        assertThat(book1.hashCode()).isEqualTo(book2.hashCode());
    }

    @Test
    @DisplayName("다른 ISBN을 가진 Book은 동등하지 않음")
    void bookInequalityWithDifferentIsbn() {
        // given
        Book book1 = Book.builder()
                .isbn("9788966262281")
                .title("Clean Code")
                .author("로버트 C. 마틴")
                .build();

        Book book2 = Book.builder()
                .isbn("9788966262298")
                .title("Clean Code")
                .author("로버트 C. 마틴")
                .build();

        // when & then
        assertThat(book1).isNotEqualTo(book2);
        assertThat(book1.hashCode()).isNotEqualTo(book2.hashCode());
    }

    @Test
    @DisplayName("toString 메서드 테스트")
    void toStringTest() {
        // given
        Book book = Book.builder()
                .isbn("9788966262281")
                .title("Clean Code")
                .author("로버트 C. 마틴")
                .build();

        // when
        String toString = book.toString();

        // then
        assertThat(toString).contains("9788966262281");
        assertThat(toString).contains("Clean Code");
        assertThat(toString).contains("로버트 C. 마틴");
    }
}