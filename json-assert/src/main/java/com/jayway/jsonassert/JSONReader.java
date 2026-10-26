package com.jayway.jsonassert;

import java.util.List;
import java.util.Map;

public interface JSONReader {

    boolean hasJsonPath(String path);

    boolean isNull(String path);

    Object get(String path);

    String getString(String path);

    Long getLong(String path);

    Boolean getBoolean(String path);

    <T> List<T> getList(String path);

    Map getMap(String path);
}
