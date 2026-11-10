package com.jayway.jsonpath.spi.mapper;

import com.jayway.jsonpath.JsonPathException;

public class MappingException extends JsonPathException {

    public MappingException(Throwable cause) {
        super(cause);
    }

    public MappingException(String message) {
        super(message);
    }
}
