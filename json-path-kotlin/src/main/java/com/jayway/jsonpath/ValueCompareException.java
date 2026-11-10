package com.jayway.jsonpath;

public class ValueCompareException extends JsonPathException {

    public ValueCompareException() {}

    public ValueCompareException(final Object left, final Object right) {
        super(
                String.format(
                        "Can not compare a %1s with a %2s",
                        left.getClass().getName(), right.getClass().getName()));
    }

    public ValueCompareException(String message) {
        super(message);
    }

    public ValueCompareException(String message, Throwable cause) {
        super(message, cause);
    }
}
