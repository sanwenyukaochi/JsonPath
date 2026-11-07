package com.jayway.jsonpath;

@SuppressWarnings("serial")
public class InvalidJsonException extends JsonPathException {

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
