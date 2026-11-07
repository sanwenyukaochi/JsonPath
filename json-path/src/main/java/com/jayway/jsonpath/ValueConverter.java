package com.jayway.jsonpath;

public interface ValueConverter {
    Object convert(Object currentValue, Configuration configuration);
}
