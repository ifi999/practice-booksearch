package com.example.booksearch.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Swagger 설정 테스트")
class SwaggerConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Swagger UI 페이지 접근 가능")
    void testSwaggerUiAccess() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("OpenAPI JSON 문서 생성")
    void testOpenApiJsonGeneration() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.info.title").value("도서 검색 API"))
                .andExpect(jsonPath("$.info.version").value("v1.0"))
                .andExpect(jsonPath("$.info.description").value("도서 검색 및 관리를 위한 REST API 서비스"));
    }

    @Test
    @DisplayName("API 경로 문서화 확인")
    void testApiPathsDocumentation() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/books']").exists())
                .andExpect(jsonPath("$.paths['/api/books/{id}']").exists())
                .andExpect(jsonPath("$.paths['/api/search/books']").exists())
                .andExpect(jsonPath("$.paths['/api/search/books/detailed']").exists())
                .andExpect(jsonPath("$.paths['/api/search/popular']").exists());
    }

    @Test
    @DisplayName("스키마 정의 확인")
    void testSchemaDefinitions() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.schemas.BookResponseDto").exists())
                .andExpect(jsonPath("$.components.schemas.ErrorResponse").exists());
    }
}