package com.example.booksearch.exception;

import com.example.booksearch.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("글로벌 예외 처리기 테스트")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        globalExceptionHandler = new GlobalExceptionHandler();
        when(request.getRequestURI()).thenReturn("/api/books/999");
    }

    @Test
    @DisplayName("BookNotFoundException - 404 응답")
    void testBookNotFoundException() {
        Long bookId = 999L;
        BookNotFoundException exception = new BookNotFoundException(bookId);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleBookNotFoundException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getError()).isEqualTo("Not Found");
        assertThat(response.getBody().getMessage()).isEqualTo("Book not found with id: " + bookId);
        assertThat(response.getBody().getPath()).isEqualTo("/api/books/999");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("InvalidSearchQueryException - 400 응답")
    void testInvalidSearchQueryException() {
        String query = "invalid||query";
        InvalidSearchQueryException exception = new InvalidSearchQueryException(query, "empty operator");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleInvalidSearchQueryException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Bad Request");
        assertThat(response.getBody().getMessage()).contains("Invalid search query");
        assertThat(response.getBody().getMessage()).contains(query);
    }

    @Test
    @DisplayName("InvalidParameterException - 400 응답")
    void testInvalidParameterException() {
        InvalidParameterException exception = new InvalidParameterException("limit", "0", "must be positive");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleInvalidParameterException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Bad Request");
        assertThat(response.getBody().getMessage()).contains("Invalid parameter 'limit'");
    }

    @Test
    @DisplayName("HttpRequestMethodNotSupportedException - 405 응답")
    void testHttpRequestMethodNotSupportedException() {
        HttpRequestMethodNotSupportedException exception = 
                new HttpRequestMethodNotSupportedException("POST");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleHttpRequestMethodNotSupportedException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(405);
        assertThat(response.getBody().getError()).isEqualTo("Method Not Allowed");
        assertThat(response.getBody().getMessage()).contains("Request method 'POST' not supported");
    }

    @Test
    @DisplayName("HttpMediaTypeNotAcceptableException - 406 응답")
    void testHttpMediaTypeNotAcceptableException() {
        HttpMediaTypeNotAcceptableException exception = new HttpMediaTypeNotAcceptableException("Media type not supported");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleHttpMediaTypeNotAcceptableException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(406);
        assertThat(response.getBody().getError()).isEqualTo("Not Acceptable");
        assertThat(response.getBody().getMessage()).isEqualTo("The requested media type is not supported. Only application/json is supported.");
    }

    @Test
    @DisplayName("IllegalArgumentException - 400 응답")
    void testIllegalArgumentException() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument provided");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleIllegalArgumentException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Bad Request");
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid argument provided");
    }

    @Test
    @DisplayName("일반 Exception - 500 응답")
    void testGenericException() {
        RuntimeException exception = new RuntimeException("Unexpected error");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleGenericException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getError()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred");
    }
}