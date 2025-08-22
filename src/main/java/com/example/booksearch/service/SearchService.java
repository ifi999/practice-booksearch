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

    public SearchService(BookRepository bookRepository, QueryParser queryParser) {
        this.bookRepository = bookRepository;
        this.queryParser = queryParser;
    }

    public Page<Book> searchBooks(String query, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            return bookRepository.findAll(pageable);
        }

        SearchQuery searchQuery = queryParser.parse(query);

        // 빈 쿼리인 경우 전체 결과 반환
        if (searchQuery.isEmpty()) {
            return bookRepository.findAll(pageable);
        }

        // 제외어만 있는 경우
        if (!searchQuery.hasIncludeTerms() && searchQuery.hasExcludeTerms()) {
            String excludeTerm = searchQuery.getExcludeTerms().get(0);
            return bookRepository.findByExcludeTerm(excludeTerm, pageable);
        }

        // 포함 키워드만 있는 경우
        if (searchQuery.hasIncludeTerms() && !searchQuery.hasExcludeTerms()) {
            return handleIncludeOnlyQuery(searchQuery, pageable);
        }

        // 포함 키워드와 제외어가 모두 있는 경우
        if (searchQuery.hasIncludeTerms() && searchQuery.hasExcludeTerms()) {
            return handleComplexQuery(searchQuery, pageable);
        }

        return bookRepository.findAll(pageable);
    }

    private Page<Book> handleIncludeOnlyQuery(SearchQuery searchQuery, Pageable pageable) {
        if (searchQuery.getIncludeTerms().size() == 1) {
            String term = searchQuery.getIncludeTerms().get(0);
            return bookRepository.findByTitleContainingOrAuthorContaining(term, term, pageable);
        } else if (searchQuery.getIncludeTerms().size() == 2) {
            String term1 = searchQuery.getIncludeTerms().get(0);
            String term2 = searchQuery.getIncludeTerms().get(1);
            return bookRepository.findByIncludeTermsOr(term1, term2, pageable);
        }
        return bookRepository.findAll(pageable);
    }

    private Page<Book> handleComplexQuery(SearchQuery searchQuery, Pageable pageable) {
        String excludeTerm = searchQuery.getExcludeTerms().get(0); // 첫 번째 제외어만 사용

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
}