package com.example.booksearch.controller;

import com.example.booksearch.dto.ApiResponse;
import com.example.booksearch.dto.BookResponseDto;
import com.example.booksearch.dto.PopularSearchDto;
import com.example.booksearch.dto.SearchResultWithMetadata;
import com.example.booksearch.service.SearchService;
import com.example.booksearch.service.SearchLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@Tag(name = "도서 검색", description = "고급 검색 기능 및 검색 통계 API")
public class SearchController {

    private final SearchService searchService;
    private final SearchLogService searchLogService;

    public SearchController(SearchService searchService, SearchLogService searchLogService) {
        this.searchService = searchService;
        this.searchLogService = searchLogService;
    }

    @Operation(summary = "도서 고급 검색", description = "복잡한 검색 쿼리로 도서를 검색합니다. OR 연산자(|)와 제외 연산자(-) 지원")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "검색 성공",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 검색 쿼리",
                    content = @Content(schema = @Schema(implementation = com.example.booksearch.dto.ErrorResponse.class)))
    })
    @GetMapping("/books")
    public ResponseEntity<ApiResponse<Page<BookResponseDto>>> searchBooks(
            @Parameter(description = "검색 쿼리 (예: 'java|spring', 'programming -beginner')", example = "java|spring")
            @RequestParam(required = false) String q,
            @Parameter(description = "페이징 정보")
            @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        
        Page<BookResponseDto> searchResults = searchService.searchBooks(q, pageable)
                .map(BookResponseDto::from);
        
        ApiResponse<Page<BookResponseDto>> response = ApiResponse.success(searchResults);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "메타데이터 포함 도서 검색", description = "검색 결과와 함께 메타데이터(검색 시간, 총 결과 수, 사용된 전략 등)를 포함하여 반환합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "검색 성공",
                    content = @Content(schema = @Schema(implementation = SearchResultWithMetadata.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 검색 쿼리",
                    content = @Content(schema = @Schema(implementation = com.example.booksearch.dto.ErrorResponse.class)))
    })
    @GetMapping("/books/detailed")
    public ResponseEntity<ApiResponse<SearchResultWithMetadata<BookResponseDto>>> searchBooksWithMetadata(
            @Parameter(description = "검색 쿼리 (예: 'java|spring', 'programming -beginner')", example = "java|spring")
            @RequestParam(required = false) String q,
            @Parameter(description = "페이징 정보")
            @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        
        SearchResultWithMetadata<com.example.booksearch.domain.Book> searchResult = 
                searchService.searchBooksWithMetadata(q, pageable);
        
        Page<BookResponseDto> bookDtos = searchResult.getData().map(BookResponseDto::from);
        
        SearchResultWithMetadata<BookResponseDto> data = 
                new SearchResultWithMetadata<>(bookDtos, searchResult.getMetadata());
        
        ApiResponse<SearchResultWithMetadata<BookResponseDto>> response = ApiResponse.success(data);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "인기 검색어 조회", description = "가장 많이 검색된 키워드 목록을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = List.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 limit 값",
                    content = @Content(schema = @Schema(implementation = com.example.booksearch.dto.ErrorResponse.class)))
    })
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<PopularSearchDto>>> getPopularSearchKeywords(
            @Parameter(description = "조회할 인기 검색어 개수", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        
        List<PopularSearchDto> popularSearches = searchLogService.getTopSearchKeywords(limit)
                .stream()
                .map(PopularSearchDto::from)
                .toList();
        
        ApiResponse<List<PopularSearchDto>> response = ApiResponse.success(popularSearches);
        return ResponseEntity.ok(response);
    }
}