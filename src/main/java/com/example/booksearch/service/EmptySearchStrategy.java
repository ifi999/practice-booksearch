package com.example.booksearch.service;

import com.example.booksearch.domain.Book;
import com.example.booksearch.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class EmptySearchStrategy implements SearchStrategy {

    private final BookRepository bookRepository;

    public EmptySearchStrategy(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public boolean canHandle(SearchQuery searchQuery) {
        return searchQuery.isEmpty();
    }

    @Override
    public Page<Book> search(SearchQuery searchQuery, Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public String getStrategyName() {
        return "EMPTY_SEARCH";
    }
}