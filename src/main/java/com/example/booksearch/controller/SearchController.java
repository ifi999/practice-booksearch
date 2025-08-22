package com.example.booksearch.controller;

import com.example.booksearch.dto.BookResponseDto;
import com.example.booksearch.service.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BookResponseDto> searchResults = searchService.searchBooks(q, pageable)
                .map(BookResponseDto::from);
        
        return ResponseEntity.ok(searchResults);
    }
}