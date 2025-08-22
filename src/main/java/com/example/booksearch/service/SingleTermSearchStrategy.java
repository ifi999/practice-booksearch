package com.example.booksearch.service;

import com.example.booksearch.domain.Book;
import com.example.booksearch.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class SingleTermSearchStrategy implements SearchStrategy {

    private final BookRepository bookRepository;

    public SingleTermSearchStrategy(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public boolean canHandle(SearchQuery searchQuery) {
        return searchQuery.hasIncludeTerms() && 
               searchQuery.getIncludeTerms().size() == 1 && 
               !searchQuery.hasExcludeTerms();
    }

    @Override
    public Page<Book> search(SearchQuery searchQuery, Pageable pageable) {
        String term = searchQuery.getIncludeTerms().get(0);
        return bookRepository.findByTitleContainingOrAuthorContaining(term, term, pageable);
    }

    @Override
    public int getPriority() {
        return 1;
    }
}