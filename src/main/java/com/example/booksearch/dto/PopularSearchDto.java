package com.example.booksearch.dto;

import com.example.booksearch.domain.SearchLog;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class PopularSearchDto {
    private String keyword;
    private Integer searchCount;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastSearchedAt;

    public PopularSearchDto(String keyword, Integer searchCount, LocalDateTime lastSearchedAt) {
        this.keyword = keyword;
        this.searchCount = searchCount;
        this.lastSearchedAt = lastSearchedAt;
    }

    public static PopularSearchDto from(SearchLog searchLog) {
        return new PopularSearchDto(
            searchLog.getKeyword(),
            searchLog.getSearchCount(),
            searchLog.getLastSearchedAt()
        );
    }

    public String getKeyword() {
        return keyword;
    }

    public Integer getSearchCount() {
        return searchCount;
    }

    public LocalDateTime getLastSearchedAt() {
        return lastSearchedAt;
    }
}