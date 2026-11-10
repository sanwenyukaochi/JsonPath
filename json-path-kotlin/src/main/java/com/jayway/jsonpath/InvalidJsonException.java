package com.jayway.jsonpath;

@SuppressWarnings("serial")
public class InvalidJsonException extends JsonPathException {

    private final String json;

    public InvalidJsonException() {
        json = null;
    }

    public InvalidJsonException(String message) {
        super(message);
        json = null;
    }

    public InvalidJsonException(String message, Throwable cause) {
        super(message, cause);
        json = null;
    }

    public InvalidJsonException(Throwable cause) {
        super(cause);
        json = null;
    }

    public InvalidJsonException(final Throwable cause, final String json) {
        super(cause);
        this.json = json;
    }

    public String getJson() {
        return json;
    }
}
