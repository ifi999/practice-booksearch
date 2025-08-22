package com.example.booksearch.service;

import com.example.booksearch.domain.Book;
import com.example.booksearch.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class ComplexSearchStrategy implements SearchStrategy {

    private final BookRepository bookRepository;

    public ComplexSearchStrategy(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public boolean canHandle(SearchQuery searchQuery) {
        return searchQuery.hasIncludeTerms() && searchQuery.hasExcludeTerms();
    }

    @Override
    public Page<Book> search(SearchQuery searchQuery, Pageable pageable) {
        String excludeTerm = searchQuery.getExcludeTerms().get(0);
        
        if (searchQuery.getIncludeTerms().size() == 1) {
            String includeTerm = searchQuery.getIncludeTerms().get(0);
            return bookRepository.findByIncludeTermAndExclude(includeTerm, excludeTerm, pageable);
        } else if (searchQuery.getIncludeTerms().size() == 2) {
            String term1 = searchQuery.getIncludeTerms().get(0);
            String term2 = searchQuery.getIncludeTerms().get(1);
            return bookRepository.findByIncludeTermsOrAndExclude(term1, term2, excludeTerm, pageable);
        }
        
        return bookRepository.findAll(pageable);
    }

    @Override
    public int getPriority() {
        return 4;
    }
}