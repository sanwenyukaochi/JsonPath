package com.jayway.jsonpath.filter;

import static java.lang.String.format;

import java.util.List;

public class FilterOutput {

    private final Object result;

    public FilterOutput(Object result) {
        this.result = result;
    }

    public boolean isList() {

        return (result instanceof List);
    }

    public Object getResult() {
        return result;
    }

    public List<Object> getResultAsList() {
        if (!isList()) {
            throw new RuntimeException(
                    format(
                            "Can not convert a %s to a %s",
                            result.getClass().getName(), List.class.getName()));
        }
        return (List<Object>) result;
    }
}
