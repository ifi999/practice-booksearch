package com.example.booksearch.service;

import com.example.booksearch.domain.Book;
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
    private final SearchLogService searchLogService;

    public SearchService(BookRepository bookRepository, QueryParser queryParser, 
                        SearchStrategyManager searchStrategyManager, SearchLogService searchLogService) {
        this.bookRepository = bookRepository;
        this.queryParser = queryParser;
        this.searchStrategyManager = searchStrategyManager;
        this.searchLogService = searchLogService;
    }

    public Page<Book> searchBooks(String query, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            return bookRepository.findAll(pageable);
        }

        // 검색어 로깅 (비동기적으로 처리)
        searchLogService.logSearch(query);

        SearchQuery searchQuery = queryParser.parse(query);
        return searchStrategyManager.search(searchQuery, pageable);
    }
}