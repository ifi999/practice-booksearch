package com.example.booksearch.service;

import com.example.booksearch.domain.Book;
import com.example.booksearch.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class NotSearchStrategy implements SearchStrategy {

    private final BookRepository bookRepository;

    public NotSearchStrategy(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public boolean canHandle(SearchQuery searchQuery) {
        return !searchQuery.hasIncludeTerms() && searchQuery.hasExcludeTerms();
    }

    @Override
    public Page<Book> search(SearchQuery searchQuery, Pageable pageable) {
        String excludeTerm = searchQuery.getExcludeTerms().get(0);
        return bookRepository.findByExcludeTerm(excludeTerm, pageable);
    }

    @Override
    public int getPriority() {
        return 3;
    }
}