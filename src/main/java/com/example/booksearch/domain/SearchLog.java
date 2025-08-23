package com.example.booksearch.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "search_logs", 
       uniqueConstraints = @UniqueConstraint(columnNames = "keyword"),
       indexes = {
           @Index(name = "idx_search_count", columnList = "searchCount"),
           @Index(name = "idx_last_searched", columnList = "lastSearchedAt")
       })
@EntityListeners(AuditingEntityListener.class)
public class SearchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String keyword;

    @NotNull
    @Column(nullable = false)
    private Integer searchCount = 1;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime lastSearchedAt;

    protected SearchLog() {}

    public SearchLog(String keyword) {
        this.keyword = keyword;
        this.searchCount = 1;
    }

    public static SearchLog of(String keyword) {
        return new SearchLog(keyword);
    }

    public void incrementSearchCount() {
        this.searchCount++;
        this.lastSearchedAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getKeyword() {
        return keyword;
    }

    public Integer getSearchCount() {
        return searchCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastSearchedAt() {
        return lastSearchedAt;
    }

    @Override
    public String toString() {
        return "SearchLog{" +
                "id=" + id +
                ", keyword='" + keyword + '\'' +
                ", searchCount=" + searchCount +
                ", lastSearchedAt=" + lastSearchedAt +
                '}';
    }
}