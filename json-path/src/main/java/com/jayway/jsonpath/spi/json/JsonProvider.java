package com.jayway.jsonpath.spi.json;

import com.jayway.jsonpath.InvalidJsonException;

import java.io.InputStream;
import java.util.Collection;

public interface JsonProvider {

    static final Object UNDEFINED = new Object();

    Object parse(String json) throws InvalidJsonException;

    Object parse(InputStream jsonStream, String charset) throws InvalidJsonException;

    String toJson(Object obj);

    Object createArray();

    Object createMap();

    boolean isArray(Object obj);

    int length(Object obj);

    Iterable<?> toIterable(Object obj);

    Collection<String> getPropertyKeys(Object obj);

    Object getArrayIndex(Object obj, int idx);

    Object getArrayIndex(Object obj, int idx, boolean unwrap);

    void setArrayIndex(Object array, int idx, Object newValue);

    Object getMapValue(Object obj, String key);

    void setProperty(Object obj, Object key, Object value);

    void removeProperty(Object obj, Object key);

    boolean isMap(Object obj);
}
