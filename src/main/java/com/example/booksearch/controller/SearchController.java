package com.example.booksearch.controller;

import com.example.booksearch.dto.BookResponseDto;
import com.example.booksearch.dto.PopularSearchDto;
import com.example.booksearch.dto.SearchResultWithMetadata;
import com.example.booksearch.service.SearchService;
import com.example.booksearch.service.SearchLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;
    private final SearchLogService searchLogService;

    public SearchController(SearchService searchService, SearchLogService searchLogService) {
        this.searchService = searchService;
        this.searchLogService = searchLogService;
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

    @GetMapping("/popular")
    public ResponseEntity<List<PopularSearchDto>> getPopularSearchKeywords(
            @RequestParam(defaultValue = "10") int limit) {
        
        List<PopularSearchDto> popularSearches = searchLogService.getTopSearchKeywords(limit)
                .stream()
                .map(PopularSearchDto::from)
                .toList();
        
        return ResponseEntity.ok(popularSearches);
    }
}