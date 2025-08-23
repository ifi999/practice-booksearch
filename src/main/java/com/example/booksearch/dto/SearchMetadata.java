package com.example.booksearch.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class SearchMetadata {
    private String query;
    private String strategy;
    private long executionTimeMs;
    private int totalResults;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime searchedAt;

    public SearchMetadata(String query, String strategy, long executionTimeMs, int totalResults) {
        this.query = query;
        this.strategy = strategy;
        this.executionTimeMs = executionTimeMs;
        this.totalResults = totalResults;
        this.searchedAt = LocalDateTime.now();
    }

    public String getQuery() {
        return query;
    }

    public String getStrategy() {
        return strategy;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public LocalDateTime getSearchedAt() {
        return searchedAt;
    }
}