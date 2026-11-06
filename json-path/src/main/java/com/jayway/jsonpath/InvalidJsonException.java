package com.jayway.jsonpath;

public class InvalidJsonException extends RuntimeException {

    public InvalidJsonException() {}

    public InvalidJsonException(String message) {
        super(message);
    }

    public InvalidJsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidJsonException(Throwable cause) {
        super(cause);
    }
}
