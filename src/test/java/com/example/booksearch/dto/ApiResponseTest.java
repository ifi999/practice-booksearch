package com.example.booksearch.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ApiResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();

    @Test
    void 성공_응답_생성_테스트() {
        String data = "test data";
        ApiResponse<String> response = ApiResponse.success(data);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("요청이 성공적으로 처리되었습니다.");
        assertThat(response.getData()).isEqualTo(data);
        assertNotNull(response.getTimestamp());
    }

    @Test
    void 커스텀_메시지로_성공_응답_생성_테스트() {
        String data = "test data";
        String customMessage = "사용자 정의 성공 메시지";
        ApiResponse<String> response = ApiResponse.success(data, customMessage);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo(customMessage);
        assertThat(response.getData()).isEqualTo(data);
        assertNotNull(response.getTimestamp());
    }

    @Test
    void 실패_응답_생성_테스트() {
        String errorMessage = "에러가 발생했습니다.";
        ApiResponse<Object> response = ApiResponse.error(400, errorMessage);

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo(errorMessage);
        assertThat(response.getData()).isNull();
        assertNotNull(response.getTimestamp());
    }

    @Test
    void 리스트_데이터로_성공_응답_생성_테스트() {
        List<String> data = Arrays.asList("item1", "item2", "item3");
        ApiResponse<List<String>> response = ApiResponse.success(data);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).hasSize(3);
        assertThat(response.getData()).containsExactly("item1", "item2", "item3");
    }

    @Test
    void JSON_직렬화_테스트() throws Exception {
        String data = "test data";
        ApiResponse<String> response = ApiResponse.success(data);

        String json = objectMapper.writeValueAsString(response);
        
        assertThat(json).contains("\"status\":200");
        assertThat(json).contains("\"success\":true");
        assertThat(json).contains("\"data\":\"test data\"");
        assertThat(json).contains("\"message\":\"요청이 성공적으로 처리되었습니다.\"");
        assertThat(json).contains("\"timestamp\":");
    }

    @Test
    void JSON_역직렬화_테스트() throws Exception {
        String json = """
            {
                "status": 200,
                "success": true,
                "message": "성공",
                "data": "test data",
                "timestamp": "2024-01-15 10:30:00"
            }
            """;

        ApiResponse<?> response = objectMapper.readValue(json, ApiResponse.class);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("성공");
        assertThat(response.getData()).isEqualTo("test data");
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    void 타임스탬프_자동_설정_테스트() {
        LocalDateTime before = LocalDateTime.now();
        ApiResponse<String> response = ApiResponse.success("test");
        LocalDateTime after = LocalDateTime.now();

        assertThat(response.getTimestamp()).isBetween(before, after);
    }

    @Test
    void null_데이터로_성공_응답_생성_테스트() {
        ApiResponse<Object> response = ApiResponse.success(null);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isNull();
    }
}