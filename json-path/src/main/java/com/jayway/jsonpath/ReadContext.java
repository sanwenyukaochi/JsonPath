package com.jayway.jsonpath;

import java.util.List;

public interface ReadContext {

    Object json();

    <T> T read(String path, Filter2... filters);

    <T> T read(JsonPath path);

    List<String> readPathList(String path, Filter2... filters);

    List<String> readPathList(JsonPath path);
}
