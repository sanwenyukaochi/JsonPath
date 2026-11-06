package com.jayway.jsonpath.spi;

import com.jayway.jsonpath.InvalidJsonException;

import java.util.List;
import java.util.Map;

public abstract class JsonProvider {

    public abstract Mode getMode();

    public abstract Object parse(String json) throws InvalidJsonException;

    public abstract Map<String, Object> createMap();

    public abstract List<Object> createList();

    public boolean isContainer(Object obj) {
        return (isList(obj) || isMap(obj));
    }

    public boolean isList(Object obj) {
        return (obj instanceof List);
    }

    public List<Object> toList(Object list) {
        return (List<Object>) list;
    }

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
