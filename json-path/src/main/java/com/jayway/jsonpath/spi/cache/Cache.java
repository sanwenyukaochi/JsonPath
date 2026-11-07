package com.jayway.jsonpath.spi.cache;

import com.jayway.jsonpath.JsonPath;

public interface Cache {

    JsonPath get(String key);

    void put(String key, JsonPath value);
}
