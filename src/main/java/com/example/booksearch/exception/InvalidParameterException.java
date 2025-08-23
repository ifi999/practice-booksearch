package com.example.booksearch.exception;

public class InvalidParameterException extends RuntimeException {
    
    public InvalidParameterException(String message) {
        super(message);
    }
    
    public InvalidParameterException(String parameter, String value, String reason) {
        super("Invalid parameter '" + parameter + "' with value '" + value + "'. Reason: " + reason);
    }
}