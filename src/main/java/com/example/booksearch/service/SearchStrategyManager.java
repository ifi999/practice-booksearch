package com.example.booksearch.service;

import com.example.booksearch.domain.Book;
import com.example.booksearch.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class SearchStrategyManager {

    private final List<SearchStrategy> strategies;

    public SearchStrategyManager(BookRepository bookRepository) {
        this.strategies = List.of(
            new EmptySearchStrategy(bookRepository),
            new SingleTermSearchStrategy(bookRepository),
            new OrSearchStrategy(bookRepository),
            new NotSearchStrategy(bookRepository),
            new ComplexSearchStrategy(bookRepository)
        );
    }

    public Page<Book> search(SearchQuery searchQuery, Pageable pageable) {
        return strategies.stream()
                .filter(strategy -> strategy.canHandle(searchQuery))
                .max(Comparator.comparing(SearchStrategy::getPriority))
                .map(strategy -> strategy.search(searchQuery, pageable))
                .orElseThrow(() -> new IllegalArgumentException("지원되지 않는 검색 쿼리입니다."));
    }

    public SearchResult searchWithMetadata(SearchQuery searchQuery, Pageable pageable) {
        SearchStrategy selectedStrategy = strategies.stream()
                .filter(strategy -> strategy.canHandle(searchQuery))
                .max(Comparator.comparing(SearchStrategy::getPriority))
                .orElseThrow(() -> new IllegalArgumentException("지원되지 않는 검색 쿼리입니다."));

        Page<Book> results = selectedStrategy.search(searchQuery, pageable);
        return new SearchResult(results, selectedStrategy.getStrategyName());
    }
}