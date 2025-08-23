package com.example.booksearch.service;

import com.example.booksearch.domain.SearchLog;
import com.example.booksearch.repository.SearchLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("SearchLogService 캐싱 테스트")
class SearchLogServiceCacheTest {

    @Autowired
    private SearchLogService searchLogService;

    @Autowired
    private CacheManager cacheManager;

    @MockBean
    private SearchLogRepository searchLogRepository;

    @Test
    @DisplayName("인기 검색어 조회 시 캐시가 적용되어야 한다")
    void shouldCachePopularKeywords() {
        // given
        int limit = 10;
        SearchLog searchLog = SearchLog.of("Spring Boot");
        List<SearchLog> popularKeywords = Collections.singletonList(searchLog);
        
        when(searchLogRepository.findTopSearchKeywords(any(Pageable.class)))
                .thenReturn(popularKeywords);

        // when - 첫 번째 호출
        List<SearchLog> firstResult = searchLogService.getTopSearchKeywords(limit);
        
        // when - 두 번째 호출 (캐시에서 가져와야 함)
        List<SearchLog> secondResult = searchLogService.getTopSearchKeywords(limit);

        // then
        assertThat(firstResult).isEqualTo(secondResult);
        assertThat(firstResult).hasSize(1);
        
        // Repository는 첫 번째 호출에서만 실행되어야 함
        verify(searchLogRepository, times(1)).findTopSearchKeywords(any(Pageable.class));
        
        // 캐시에 값이 저장되었는지 확인
        assertThat(cacheManager.getCache("popularKeywords").get(limit)).isNotNull();
    }

    @Test
    @DisplayName("검색 로깅 시 인기 검색어 캐시가 무효화되어야 한다")
    void shouldEvictCacheWhenLoggingSearch() {
        // given
        int limit = 5;
        String keyword = "Java";
        
        SearchLog existingLog = SearchLog.of(keyword);
        when(searchLogRepository.findByKeyword(anyString())).thenReturn(Optional.of(existingLog));
        when(searchLogRepository.save(any(SearchLog.class))).thenReturn(existingLog);
        when(searchLogRepository.findTopSearchKeywords(any(Pageable.class)))
                .thenReturn(Collections.singletonList(existingLog));

        // when - 먼저 인기 검색어를 캐시에 저장
        searchLogService.getTopSearchKeywords(limit);
        assertThat(cacheManager.getCache("popularKeywords").get(limit)).isNotNull();
        
        // when - 검색 로깅 (캐시 무효화 트리거)
        searchLogService.logSearch(keyword);
        
        // then - 캐시가 무효화되었는지 확인
        assertThat(cacheManager.getCache("popularKeywords").get(limit)).isNull();
    }

    @Test
    @DisplayName("다른 limit으로 조회 시 각각 캐시되어야 한다")
    void shouldCacheDifferentLimits() {
        // given
        SearchLog searchLog = SearchLog.of("Spring Boot");
        List<SearchLog> popularKeywords = Collections.singletonList(searchLog);
        
        when(searchLogRepository.findTopSearchKeywords(any(Pageable.class)))
                .thenReturn(popularKeywords);

        // when
        List<SearchLog> result5 = searchLogService.getTopSearchKeywords(5);
        List<SearchLog> result10 = searchLogService.getTopSearchKeywords(10);
        
        // 같은 limit으로 다시 요청
        List<SearchLog> cachedResult5 = searchLogService.getTopSearchKeywords(5);

        // then
        assertThat(result5).isEqualTo(cachedResult5);
        
        // limit 5와 10으로 각각 한 번씩, 총 2번 호출
        verify(searchLogRepository, times(2)).findTopSearchKeywords(any(Pageable.class));
        
        // 각 limit별로 캐시 확인
        assertThat(cacheManager.getCache("popularKeywords").get(5)).isNotNull();
        assertThat(cacheManager.getCache("popularKeywords").get(10)).isNotNull();
    }
}