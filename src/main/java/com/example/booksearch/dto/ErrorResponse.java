package com.example.booksearch.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * HTTP 에러 응답의 표준 형식
 */
@Schema(
    title = "Error Response",
    description = "HTTP 에러 응답의 표준 형식을 나타내는 DTO 클래스"
)
public class ErrorResponse {
    
    @Schema(description = "HTTP 상태 코드", example = "400")
    private int status;
    
    @Schema(description = "HTTP 상태 메시지", example = "Bad Request")
    private String error;
    
    @Schema(description = "상세 에러 메시지", example = "검색어는 필수 입력값입니다.")
    private String message;
    
    @Schema(description = "요청된 API 경로", example = "/api/books/999")
    private String path;
    
    @Schema(description = "에러 발생 시간", example = "2024-01-15 10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String error, String message, String path) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}