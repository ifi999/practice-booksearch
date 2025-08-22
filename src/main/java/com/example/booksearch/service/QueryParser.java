package com.example.booksearch.service;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class QueryParser {

    private static final Pattern EXCLUDE_PATTERN = Pattern.compile("-\\s*([^\\s-]+)");
    private static final String OR_SEPARATOR = "\\|";
    
    public SearchQuery parse(String query) {
        if (!StringUtils.hasText(query) || query.trim().equals("-") || query.trim().equals("|")) {
            return new SearchQuery(new ArrayList<>(), new ArrayList<>());
        }

        String trimmedQuery = query.trim();
        
        // 제외어 추출
        List<String> excludeTerms = extractExcludeTerms(trimmedQuery);
        
        // 제외어를 제거한 나머지 부분에서 포함 키워드 추출
        String includeQuery = removeExcludeTerms(trimmedQuery);
        List<String> includeTerms = extractIncludeTerms(includeQuery);
        
        // 최대 2개 키워드 제한 검증
        validateKeywordLimit(includeTerms);
        
        return new SearchQuery(includeTerms, excludeTerms);
    }
    
    private List<String> extractExcludeTerms(String query) {
        List<String> excludeTerms = new ArrayList<>();
        Matcher matcher = EXCLUDE_PATTERN.matcher(query);
        
        while (matcher.find()) {
            String excludeTerm = matcher.group(1).trim();
            if (StringUtils.hasText(excludeTerm) && !excludeTerm.equals("")) {
                excludeTerms.add(excludeTerm);
            }
        }
        
        return excludeTerms;
    }
    
    private String removeExcludeTerms(String query) {
        return EXCLUDE_PATTERN.matcher(query).replaceAll("").trim();
    }
    
    private List<String> extractIncludeTerms(String includeQuery) {
        if (!StringUtils.hasText(includeQuery)) {
            return new ArrayList<>();
        }
        
        // OR 연산자로 분리
        List<String> terms = Arrays.stream(includeQuery.split(OR_SEPARATOR))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
        
        // 공백으로 분리된 여러 키워드도 처리
        if (terms.size() == 1) {
            String[] spaceSeparated = terms.get(0).split("\\s+");
            if (spaceSeparated.length > 1) {
                terms = Arrays.stream(spaceSeparated)
                        .filter(StringUtils::hasText)
                        .collect(Collectors.toList());
            }
        }
        
        return terms;
    }
    
    private void validateKeywordLimit(List<String> includeTerms) {
        if (includeTerms.size() > 2) {
            throw new IllegalArgumentException("최대 2개의 키워드만 지원합니다.");
        }
    }
}