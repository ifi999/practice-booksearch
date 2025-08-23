package com.example.booksearch.repository;

import com.example.booksearch.domain.SearchLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {

    Optional<SearchLog> findByKeyword(String keyword);

    @Query("SELECT s FROM SearchLog s ORDER BY s.searchCount DESC, s.lastSearchedAt DESC")
    List<SearchLog> findTopSearchKeywords(Pageable pageable);

    @Query("SELECT s FROM SearchLog s WHERE s.searchCount >= :minCount ORDER BY s.searchCount DESC")
    List<SearchLog> findPopularSearchKeywords(int minCount, Pageable pageable);

    boolean existsByKeyword(String keyword);
}