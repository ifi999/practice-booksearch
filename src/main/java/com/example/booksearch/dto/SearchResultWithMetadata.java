package com.example.booksearch.dto;

import org.springframework.data.domain.Page;

public class SearchResultWithMetadata<T> {
    private Page<T> data;
    private SearchMetadata metadata;

    public SearchResultWithMetadata(Page<T> data, SearchMetadata metadata) {
        this.data = data;
        this.metadata = metadata;
    }

    public Page<T> getData() {
        return data;
    }

    public SearchMetadata getMetadata() {
        return metadata;
    }
}