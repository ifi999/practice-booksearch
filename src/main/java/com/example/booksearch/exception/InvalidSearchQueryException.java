package com.example.booksearch.exception;

public class InvalidSearchQueryException extends RuntimeException {
    
    public InvalidSearchQueryException(String message) {
        super(message);
    }
    
    public InvalidSearchQueryException(String query, String reason) {
        super("Invalid search query: '" + query + "'. Reason: " + reason);
    }
}