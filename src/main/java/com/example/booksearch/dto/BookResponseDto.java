package com.example.booksearch.dto;

import com.example.booksearch.domain.Book;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookResponseDto(
        Long id,
        String isbn,
        String title,
        String subtitle,
        String author,
        String publisher,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate publicationDate,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime updatedAt
) {
    public static BookResponseDto from(Book book) {
        return new BookResponseDto(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getSubtitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getPublicationDate(),
                book.getCreatedAt(),
                book.getUpdatedAt()
        );
    }
}