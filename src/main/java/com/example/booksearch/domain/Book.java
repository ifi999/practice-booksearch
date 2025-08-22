package com.example.booksearch.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 13)
    @NotBlank(message = "ISBN은 필수 입력값입니다.")
    @Size(min = 10, max = 13, message = "ISBN은 10-13자리여야 합니다.")
    private String isbn;

    @Column(nullable = false)
    @NotBlank(message = "제목은 필수 입력값입니다.")
    private String title;

    private String subtitle;

    @Column(nullable = false)
    @NotBlank(message = "저자는 필수 입력값입니다.")
    private String author;

    private String publisher;

    private LocalDate publicationDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected Book() {}

    private Book(Builder builder) {
        validateRequired(builder.isbn, "ISBN은 필수 입력값입니다.");
        validateRequired(builder.title, "제목은 필수 입력값입니다.");
        validateRequired(builder.author, "저자는 필수 입력값입니다.");
        
        this.isbn = builder.isbn;
        this.title = builder.title;
        this.subtitle = builder.subtitle;
        this.author = builder.author;
        this.publisher = builder.publisher;
        this.publicationDate = builder.publicationDate;
    }

    private void validateRequired(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String isbn;
        private String title;
        private String subtitle;
        private String author;
        private String publisher;
        private LocalDate publicationDate;

        public Builder isbn(String isbn) {
            this.isbn = isbn;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder subtitle(String subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder publisher(String publisher) {
            this.publisher = publisher;
            return this;
        }

        public Builder publicationDate(LocalDate publicationDate) {
            this.publicationDate = publicationDate;
            return this;
        }

        public Book build() {
            return new Book(this);
        }
    }

    public Long getId() {
        return id;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}