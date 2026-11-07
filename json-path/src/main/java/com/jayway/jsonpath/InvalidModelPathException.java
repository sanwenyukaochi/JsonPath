package com.jayway.jsonpath;

public class InvalidModelPathException extends RuntimeException {

    public InvalidModelPathException() {
        super();
    }

    public InvalidModelPathException(String message) {
        super(message);
    }

    public InvalidModelPathException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidModelPathException(Throwable cause) {
        super(cause);
    }
}
