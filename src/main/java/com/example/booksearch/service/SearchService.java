package com.example.booksearch.service;

import com.example.booksearch.domain.Book;
import com.example.booksearch.dto.SearchMetadata;
import com.example.booksearch.dto.SearchResultWithMetadata;
import com.example.booksearch.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SearchService {

    private final BookRepository bookRepository;
    private final QueryParser queryParser;
    private final SearchStrategyManager searchStrategyManager;

    public SearchService(BookRepository bookRepository, QueryParser queryParser, SearchStrategyManager searchStrategyManager) {
        this.bookRepository = bookRepository;
        this.queryParser = queryParser;
        this.searchStrategyManager = searchStrategyManager;
    }

    public Page<Book> searchBooks(String query, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            return bookRepository.findAll(pageable);
        }

        SearchQuery searchQuery = queryParser.parse(query);
        return searchStrategyManager.search(searchQuery, pageable);
    }

    public SearchResultWithMetadata<Book> searchBooksWithMetadata(String query, Pageable pageable) {
        long startTime = System.currentTimeMillis();
        
        if (query == null || query.trim().isEmpty()) {
            Page<Book> results = bookRepository.findAll(pageable);
            long executionTime = System.currentTimeMillis() - startTime;
            
            SearchMetadata metadata = new SearchMetadata(
                query, 
                "ALL_BOOKS", 
                executionTime, 
                (int) results.getTotalElements()
            );
            
            return new SearchResultWithMetadata<>(results, metadata);
        }

        SearchQuery searchQuery = queryParser.parse(query);
        SearchResult searchResult = searchStrategyManager.searchWithMetadata(searchQuery, pageable);
        
        long executionTime = System.currentTimeMillis() - startTime;
        
        SearchMetadata metadata = new SearchMetadata(
            query,
            searchResult.getStrategyUsed(),
            executionTime,
            (int) searchResult.getBooks().getTotalElements()
        );
        
        return new SearchResultWithMetadata<>(searchResult.getBooks(), metadata);
    }
}