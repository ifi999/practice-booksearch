package com.example.booksearch.service;

import com.example.booksearch.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchStrategy {
    boolean canHandle(SearchQuery searchQuery);
    Page<Book> search(SearchQuery searchQuery, Pageable pageable);
    int getPriority();
    String getStrategyName();
}