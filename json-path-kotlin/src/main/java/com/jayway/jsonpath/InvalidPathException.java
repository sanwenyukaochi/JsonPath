package com.jayway.jsonpath;

@SuppressWarnings("serial")
public class InvalidPathException extends JsonPathException {

    public InvalidPathException() {}

    public InvalidPathException(String message) {
        super(message);
    }

    public InvalidPathException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPathException(Throwable cause) {
        super(cause);
    }
}
