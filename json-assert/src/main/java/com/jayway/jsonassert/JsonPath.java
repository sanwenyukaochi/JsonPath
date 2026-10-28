package com.jayway.jsonassert;

import java.util.List;
import java.util.Map;

public interface JsonPath {

    JsonPath getReader(String path);

    boolean hasJsonPath(String path);

    boolean isNull(String path);

    <T> T get(String path);

    String getString(String path);

    Long getLong(String path);

    Double getDouble(String path);

    Boolean getBoolean(String path);

    <T> List<T> getList(String path);

    Map<String, Object> getMap(String path);
}
