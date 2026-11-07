package com.jayway.jsonpath;

public interface ValueConverter {

    public Object convert(Object currentValue, Configuration configuration);
}
