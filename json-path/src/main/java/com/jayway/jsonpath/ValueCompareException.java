package com.jayway.jsonpath;

public class ValueCompareException extends JsonPathException {

    public ValueCompareException() {}

    public ValueCompareException(String message) {
        super(message);
    }

    public ValueCompareException(String message, Throwable cause) {
        super(message, cause);
    }
}
