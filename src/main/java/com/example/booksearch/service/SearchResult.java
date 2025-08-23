package com.example.booksearch.service;

import com.example.booksearch.domain.Book;
import org.springframework.data.domain.Page;

public class SearchResult {
    private final Page<Book> books;
    private final String strategyUsed;

    public SearchResult(Page<Book> books, String strategyUsed) {
        this.books = books;
        this.strategyUsed = strategyUsed;
    }

    public Page<Book> getBooks() {
        return books;
    }

    public String getStrategyUsed() {
        return strategyUsed;
    }
}