package com.example.booksearch.controller;

import com.example.booksearch.dto.BookResponseDto;
import com.example.booksearch.dto.SearchResultWithMetadata;
import com.example.booksearch.service.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/books")
    public ResponseEntity<Page<BookResponseDto>> searchBooks(
            @RequestParam(required = false) String q,
            @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        
        Page<BookResponseDto> searchResults = searchService.searchBooks(q, pageable)
                .map(BookResponseDto::from);
        
        return ResponseEntity.ok(searchResults);
    }

    @GetMapping("/books/detailed")
    public ResponseEntity<SearchResultWithMetadata<BookResponseDto>> searchBooksWithMetadata(
            @RequestParam(required = false) String q,
            @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        
        SearchResultWithMetadata<com.example.booksearch.domain.Book> searchResult = 
                searchService.searchBooksWithMetadata(q, pageable);
        
        Page<BookResponseDto> bookDtos = searchResult.getData().map(BookResponseDto::from);
        
        SearchResultWithMetadata<BookResponseDto> response = 
                new SearchResultWithMetadata<>(bookDtos, searchResult.getMetadata());
        
        return ResponseEntity.ok(response);
    }
}