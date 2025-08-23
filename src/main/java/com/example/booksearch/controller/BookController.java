package com.example.booksearch.controller;

import com.example.booksearch.dto.BookResponseDto;
import com.example.booksearch.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@Tag(name = "도서 관리", description = "도서 정보 조회 및 관리 API")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "도서 목록 조회", description = "키워드로 도서를 검색하거나 전체 도서 목록을 페이징하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", 
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터",
                    content = @Content(schema = @Schema(implementation = com.example.booksearch.dto.ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<Page<BookResponseDto>> getBooks(
            @Parameter(description = "검색 키워드 (제목 또는 저자)", example = "자바")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "페이징 정보")
            @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        
        Page<BookResponseDto> books = bookService.findBooks(keyword, pageable)
                .map(BookResponseDto::from);
        
        return ResponseEntity.ok(books);
    }

    @Operation(summary = "도서 상세 조회", description = "도서 ID로 특정 도서의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = BookResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.example.booksearch.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 ID 형식",
                    content = @Content(schema = @Schema(implementation = com.example.booksearch.dto.ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> getBookById(
            @Parameter(description = "도서 ID", example = "1", required = true)
            @PathVariable Long id) {
        BookResponseDto book = BookResponseDto.from(bookService.findById(id));
        return ResponseEntity.ok(book);
    }
}