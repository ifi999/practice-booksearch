package com.example.booksearch.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("QueryParser 단위 테스트")
class QueryParserTest {

    private final QueryParser queryParser = new QueryParser();

    @Test
    @DisplayName("단일 키워드 파싱")
    void parseSingleKeyword() {
        // when
        SearchQuery result = queryParser.parse("spring");

        // then
        assertThat(result.getIncludeTerms()).containsExactly("spring");
        assertThat(result.getExcludeTerms()).isEmpty();
    }

    @Test
    @DisplayName("OR 연산자 파싱 - 2개 키워드")
    void parseOrOperator() {
        // when
        SearchQuery result = queryParser.parse("java|python");

        // then
        assertThat(result.getIncludeTerms()).containsExactlyInAnyOrder("java", "python");
        assertThat(result.getExcludeTerms()).isEmpty();
    }

    @Test
    @DisplayName("NOT 연산자 파싱 - 키워드와 제외어")
    void parseNotOperator() {
        // when
        SearchQuery result = queryParser.parse("programming -beginner");

        // then
        assertThat(result.getIncludeTerms()).containsExactly("programming");
        assertThat(result.getExcludeTerms()).containsExactly("beginner");
    }

    @Test
    @DisplayName("복합 연산 파싱 - OR와 NOT 조합")
    void parseComplexQuery() {
        // when
        SearchQuery result = queryParser.parse("java|spring -tutorial");

        // then
        assertThat(result.getIncludeTerms()).containsExactlyInAnyOrder("java", "spring");
        assertThat(result.getExcludeTerms()).containsExactly("tutorial");
    }

    @Test
    @DisplayName("여러 제외어 파싱")
    void parseMultipleExcludeTerms() {
        // when
        SearchQuery result = queryParser.parse("programming -beginner -tutorial");

        // then
        assertThat(result.getIncludeTerms()).containsExactly("programming");
        assertThat(result.getExcludeTerms()).containsExactlyInAnyOrder("beginner", "tutorial");
    }

    @Test
    @DisplayName("공백이 포함된 쿼리 파싱")
    void parseQueryWithSpaces() {
        // when
        SearchQuery result = queryParser.parse(" java | python -tutorial ");

        // then
        assertThat(result.getIncludeTerms()).containsExactlyInAnyOrder("java", "python");
        assertThat(result.getExcludeTerms()).containsExactly("tutorial");
    }

    @Test
    @DisplayName("빈 문자열 파싱")
    void parseEmptyString() {
        // when
        SearchQuery result = queryParser.parse("");

        // then
        assertThat(result.getIncludeTerms()).isEmpty();
        assertThat(result.getExcludeTerms()).isEmpty();
    }

    @Test
    @DisplayName("null 파싱")
    void parseNull() {
        // when
        SearchQuery result = queryParser.parse(null);

        // then
        assertThat(result.getIncludeTerms()).isEmpty();
        assertThat(result.getExcludeTerms()).isEmpty();
    }

    @Test
    @DisplayName("최대 2개 키워드 제한 검증 - 3개 OR 키워드")
    void validateMaxTwoKeywordsLimit() {
        // when & then
        assertThatThrownBy(() -> queryParser.parse("java|python|javascript"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("최대 2개의 키워드만 지원합니다.");
    }

    @Test
    @DisplayName("최대 2개 키워드 제한 검증 - 3개 포함 키워드")
    void validateMaxTwoIncludeTermsLimit() {
        // when & then
        assertThatThrownBy(() -> queryParser.parse("java spring python"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("최대 2개의 키워드만 지원합니다.");
    }

    @Test
    @DisplayName("제외어만 있는 쿼리")
    void parseExcludeOnlyQuery() {
        // when
        SearchQuery result = queryParser.parse("-beginner");

        // then
        assertThat(result.getIncludeTerms()).isEmpty();
        assertThat(result.getExcludeTerms()).containsExactly("beginner");
    }

    @Test
    @DisplayName("OR 연산자가 포함된 제외어")
    void parseExcludeTermsWithOrOperator() {
        // when
        SearchQuery result = queryParser.parse("programming -beginner|tutorial");

        // then
        assertThat(result.getIncludeTerms()).containsExactly("programming");
        assertThat(result.getExcludeTerms()).containsExactly("beginner|tutorial");
    }

    @Test
    @DisplayName("특수문자가 포함된 키워드")
    void parseKeywordWithSpecialCharacters() {
        // when
        SearchQuery result = queryParser.parse("C++ -C#");

        // then
        assertThat(result.getIncludeTerms()).containsExactly("C++");
        assertThat(result.getExcludeTerms()).containsExactly("C#");
    }

    @Test
    @DisplayName("OR 연산자만 있는 잘못된 쿼리")
    void parseInvalidOrOnlyQuery() {
        // when
        SearchQuery result = queryParser.parse("|");

        // then
        assertThat(result.getIncludeTerms()).isEmpty();
        assertThat(result.getExcludeTerms()).isEmpty();
    }

    @Test
    @DisplayName("NOT 연산자만 있는 잘못된 쿼리")
    void parseInvalidNotOnlyQuery() {
        // when
        SearchQuery result = queryParser.parse("-");

        // then
        assertThat(result.getIncludeTerms()).isEmpty();
        assertThat(result.getExcludeTerms()).isEmpty();
    }
}