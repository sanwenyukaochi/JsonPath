package com.jayway.jsonpath.spi.cache;

import com.jayway.jsonpath.JsonPath;

public interface Cache {

    public JsonPath get(String key);

    public void put(String key, JsonPath value);
}
