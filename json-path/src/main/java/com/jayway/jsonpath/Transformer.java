package com.jayway.jsonpath;

public interface Transformer<T> {

    public Object transform(T obj);
}
