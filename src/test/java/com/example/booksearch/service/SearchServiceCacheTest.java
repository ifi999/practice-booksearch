package com.example.booksearch.service;

import com.example.booksearch.domain.Book;
import com.example.booksearch.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("SearchService 캐싱 테스트")
class SearchServiceCacheTest {

    @Autowired
    private SearchService searchService;

    @Autowired
    private CacheManager cacheManager;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private QueryParser queryParser;

    @MockBean
    private SearchStrategyManager searchStrategyManager;

    @MockBean
    private SearchLogService searchLogService;

    @Test
    @DisplayName("같은 검색어로 두 번 검색 시 캐시가 적용되어야 한다")
    void shouldCacheSearchResults() {
        // given
        String query = "Spring Boot";
        Pageable pageable = PageRequest.of(0, 10);
        
        Book book = Book.builder()
                .title("Test Title")
                .author("Test Author")
                .isbn("1234567890123")
                .build();
        Page<Book> searchResults = new PageImpl<>(Collections.singletonList(book));
        
        SearchQuery searchQuery = new SearchQuery(java.util.Arrays.asList("Spring", "Boot"), java.util.Collections.emptyList());
        when(queryParser.parse(anyString())).thenReturn(searchQuery);
        when(searchStrategyManager.search(any(SearchQuery.class), any(Pageable.class)))
                .thenReturn(searchResults);

        // when - 첫 번째 호출
        Page<Book> firstResult = searchService.searchBooks(query, pageable);
        
        // when - 두 번째 호출 (캐시에서 가져와야 함)
        Page<Book> secondResult = searchService.searchBooks(query, pageable);

        // then
        assertThat(firstResult).isEqualTo(secondResult);
        assertThat(firstResult.getContent()).hasSize(1);
        
        // SearchStrategyManager는 첫 번째 호출에서만 실행되어야 함
        verify(searchStrategyManager, times(1)).search(any(SearchQuery.class), any(Pageable.class));
        
        // 캐시에 값이 저장되었는지 확인
        assertThat(cacheManager.getCache("searchResults")).isNotNull();
    }

    @Test
    @DisplayName("다른 페이지 요청 시 각각 캐시되어야 한다")
    void shouldCacheDifferentPageRequests() {
        // given
        String query = "Java";
        Pageable firstPage = PageRequest.of(0, 10);
        Pageable secondPage = PageRequest.of(1, 10);
        
        Book book = Book.builder()
                .title("Java Book")
                .author("Java Author")
                .isbn("9876543210987")
                .build();
        Page<Book> searchResults = new PageImpl<>(Collections.singletonList(book));
        
        SearchQuery searchQuery = new SearchQuery(java.util.Arrays.asList("Java"), java.util.Collections.emptyList());
        when(queryParser.parse(anyString())).thenReturn(searchQuery);
        when(searchStrategyManager.search(any(SearchQuery.class), any(Pageable.class)))
                .thenReturn(searchResults);

        // when
        Page<Book> firstPageResult = searchService.searchBooks(query, firstPage);
        Page<Book> secondPageResult = searchService.searchBooks(query, secondPage);
        
        // 같은 페이지를 다시 요청
        Page<Book> firstPageCached = searchService.searchBooks(query, firstPage);

        // then
        assertThat(firstPageResult).isEqualTo(firstPageCached);
        
        // 첫 번째 페이지는 캐시에서, 두 번째 페이지는 새로 조회되므로 총 2번 호출
        verify(searchStrategyManager, times(2)).search(any(SearchQuery.class), any(Pageable.class));
    }
}