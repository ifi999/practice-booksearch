package com.example.booksearch.service;

import com.example.booksearch.domain.Book;
import com.example.booksearch.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class OrSearchStrategy implements SearchStrategy {

    private final BookRepository bookRepository;

    public OrSearchStrategy(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public boolean canHandle(SearchQuery searchQuery) {
        return searchQuery.hasIncludeTerms() && 
               searchQuery.getIncludeTerms().size() == 2 && 
               !searchQuery.hasExcludeTerms();
    }

    @Override
    public Page<Book> search(SearchQuery searchQuery, Pageable pageable) {
        String term1 = searchQuery.getIncludeTerms().get(0);
        String term2 = searchQuery.getIncludeTerms().get(1);
        return bookRepository.findByIncludeTermsOr(term1, term2, pageable);
    }

    @Override
    public int getPriority() {
        return 2;
    }
}