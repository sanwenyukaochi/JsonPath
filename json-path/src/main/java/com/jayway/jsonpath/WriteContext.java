package com.jayway.jsonpath;

public interface WriteContext {

    Configuration configuration();

    <T> T json();

    DocumentContext set(String path, Object newValue, Predicate... filters);

    DocumentContext delete(String path, Predicate... filters);

    DocumentContext add(String path, Object value, Predicate... filters);

    DocumentContext put(String path, String key, Object value, Predicate... filters);
}
