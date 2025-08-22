package com.example.booksearch.service;

import java.util.List;

public class SearchQuery {
    private final List<String> includeTerms;
    private final List<String> excludeTerms;

    public SearchQuery(List<String> includeTerms, List<String> excludeTerms) {
        this.includeTerms = includeTerms;
        this.excludeTerms = excludeTerms;
    }

    public List<String> getIncludeTerms() {
        return includeTerms;
    }

    public List<String> getExcludeTerms() {
        return excludeTerms;
    }

    public boolean isEmpty() {
        return includeTerms.isEmpty() && excludeTerms.isEmpty();
    }

    public boolean hasIncludeTerms() {
        return !includeTerms.isEmpty();
    }

    public boolean hasExcludeTerms() {
        return !excludeTerms.isEmpty();
    }

    @Override
    public String toString() {
        return "SearchQuery{" +
                "includeTerms=" + includeTerms +
                ", excludeTerms=" + excludeTerms +
                '}';
    }
}