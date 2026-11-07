package com.jayway.jsonpath;

public interface WriteContext {

    Configuration configuration();

    <T> T json();

    String jsonString();

    DocumentContext set(String path, Object newValue, Predicate... filters);

    DocumentContext set(JsonPath path, Object newValue);

    DocumentContext map(String path, MapFunction mapFunction, Predicate... filters);

    DocumentContext map(JsonPath path, MapFunction mapFunction);

    DocumentContext delete(String path, Predicate... filters);

    DocumentContext delete(JsonPath path);

    DocumentContext add(String path, Object value, Predicate... filters);

    DocumentContext add(JsonPath path, Object value);

    DocumentContext put(String path, String key, Object value, Predicate... filters);

    DocumentContext put(JsonPath path, String key, Object value);

    DocumentContext renameKey(
            String path, String oldKeyName, String newKeyName, Predicate... filters);

    DocumentContext renameKey(JsonPath path, String oldKeyName, String newKeyName);
}
