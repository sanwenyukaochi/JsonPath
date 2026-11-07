package com.jayway.jsonpath;

@SuppressWarnings("serial")
public class InvalidModelException extends RuntimeException {

    public InvalidModelException() {
        super();
    }

    public InvalidModelException(String message) {
        super(message);
    }

    public InvalidModelException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidModelException(Throwable cause) {
        super(cause);
    }
}
