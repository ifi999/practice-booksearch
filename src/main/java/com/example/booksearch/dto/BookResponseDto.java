package com.example.booksearch.dto;

import com.example.booksearch.domain.Book;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "도서 정보 응답 DTO")
public record BookResponseDto(
        @Schema(description = "도서 ID", example = "1")
        Long id,
        @Schema(description = "ISBN", example = "9788966262281")
        String isbn,
        @Schema(description = "제목", example = "자바 프로그래밍")
        String title,
        @Schema(description = "부제목", example = "객체지향 개발의 원리와 이해")
        String subtitle,
        @Schema(description = "저자", example = "김상형")
        String author,
        @Schema(description = "출판사", example = "한빛미디어")
        String publisher,
        @Schema(description = "출간일", example = "2020-01-15")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate publicationDate,
        @Schema(description = "생성일시", example = "2024-01-15T10:30:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,
        @Schema(description = "수정일시", example = "2024-01-15T10:30:00")
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