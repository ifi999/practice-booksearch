package com.example.booksearch.service;

import com.example.booksearch.domain.SearchLog;
import com.example.booksearch.repository.SearchLogRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SearchLogService {

    private final SearchLogRepository searchLogRepository;

    public SearchLogService(SearchLogRepository searchLogRepository) {
        this.searchLogRepository = searchLogRepository;
    }

    @CacheEvict(value = "popularKeywords", allEntries = true)
    public void logSearch(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return;
        }

        String normalizedKeyword = normalizeKeyword(keyword);
        
        Optional<SearchLog> existingLog = searchLogRepository.findByKeyword(normalizedKeyword);
        
        if (existingLog.isPresent()) {
            existingLog.get().incrementSearchCount();
            searchLogRepository.save(existingLog.get());
        } else {
            SearchLog newLog = SearchLog.of(normalizedKeyword);
            searchLogRepository.save(newLog);
        }
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "popularKeywords", key = "#limit")
    public List<SearchLog> getTopSearchKeywords(int limit) {
        return searchLogRepository.findTopSearchKeywords(PageRequest.of(0, limit));
    }

    @Transactional(readOnly = true)
    public List<SearchLog> getPopularSearchKeywords(int minCount, int limit) {
        return searchLogRepository.findPopularSearchKeywords(minCount, PageRequest.of(0, limit));
    }

    private String normalizeKeyword(String keyword) {
        return keyword.trim().toLowerCase();
    }
}