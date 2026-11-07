package com.jayway.jsonpath;

public interface ReadContext {

    Object json();

    <T> T read(String path, Predicate... filters);

    <T> T read(String path, Class<T> type, Predicate... filters);

    <T> T read(JsonPath path);

    <T> T read(JsonPath path, Class<T> type);
}
