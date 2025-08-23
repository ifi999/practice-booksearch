package com.example.booksearch.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * API 응답의 표준 형식
 */
@Schema(
    title = "API Response",
    description = "모든 API 응답의 표준 형식을 나타내는 제네릭 래퍼 클래스"
)
public class ApiResponse<T> {
    
    @Schema(description = "HTTP 상태 코드", example = "200")
    private int status;
    
    @Schema(description = "요청 성공 여부", example = "true")
    private boolean success;
    
    @Schema(description = "응답 메시지", example = "요청이 성공적으로 처리되었습니다.")
    private String message;
    
    @Schema(description = "응답 데이터")
    private T data;
    
    @Schema(description = "응답 생성 시간", example = "2024-01-15 10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    // 기본 생성자
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    // 전체 필드 생성자
    public ApiResponse(int status, boolean success, String message, T data) {
        this();
        this.status = status;
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // 성공 응답 생성 - 기본 메시지
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, true, "요청이 성공적으로 처리되었습니다.", data);
    }

    // 성공 응답 생성 - 커스텀 메시지
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(200, true, message, data);
    }

    // 에러 응답 생성
    public static <T> ApiResponse<T> error(int status, String message) {
        return new ApiResponse<>(status, false, message, null);
    }

    // Getter와 Setter
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}