package com.jayway.jsonpath;

public class InvalidCriteriaException extends RuntimeException {
    public InvalidCriteriaException() {}

    public InvalidCriteriaException(String message) {
        super(message);
    }

    public InvalidCriteriaException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCriteriaException(Throwable cause) {
        super(cause);
    }
}
