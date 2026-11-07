package com.jayway.jsonpath;

public interface WriteContext {

    Configuration configuration();

    <T> T json();

    String jsonString();

    DocumentContext set(String path, Object newValue, Predicate... filters);

    DocumentContext set(JsonPath path, Object newValue);

    DocumentContext delete(String path, Predicate... filters);

    DocumentContext delete(JsonPath path);

    DocumentContext add(String path, Object value, Predicate... filters);

    DocumentContext add(JsonPath path, Object value);

    DocumentContext put(String path, String key, Object value, Predicate... filters);

    DocumentContext put(JsonPath path, String key, Object value);
}
