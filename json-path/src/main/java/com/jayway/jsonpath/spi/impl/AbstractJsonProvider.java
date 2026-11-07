package com.jayway.jsonpath.spi.impl;

import com.jayway.jsonpath.spi.JsonProvider;

import java.util.List;
import java.util.Map;

public abstract class AbstractJsonProvider implements JsonProvider {

    public boolean isContainer(Object obj) {
        return (isList(obj) || isMap(obj));
    }

    public boolean isList(Object obj) {
        return (obj instanceof List);
    }

    @SuppressWarnings({"unchecked"})
    public List<Object> toList(Object list) {
        return (List<Object>) list;
    }

    @SuppressWarnings({"unchecked"})
    public Map<String, Object> toMap(Object map) {
        return (Map<String, Object>) map;
    }

    public Object getMapValue(Object map, String key) {
        return toMap(map).get(key);
    }

    public boolean isMap(Object obj) {
        return (obj instanceof Map);
    }
}
