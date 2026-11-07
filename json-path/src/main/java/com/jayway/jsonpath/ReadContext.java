package com.jayway.jsonpath;

public interface ReadContext {

    Object json();

    <T> T read(String path, Filter... filters);

    <T> T read(JsonPath path);
}
