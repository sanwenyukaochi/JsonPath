package com.jayway.jsonpath.spi.json;

import com.jayway.jsonpath.InvalidJsonException;

import java.io.InputStream;
import java.util.Collection;

public interface JsonProvider {

    static final Object UNDEFINED = new Object();

    Object parse(String json) throws InvalidJsonException;

    Object parse(InputStream jsonStream) throws InvalidJsonException;

    String toJson(Object obj);

    Object createNull();

    Object createArray();

    boolean isArray(Object obj);

    int length(Object obj);

    Iterable<?> toIterable(Object obj);

    Collection<String> getPropertyKeys(Object obj);

    Object getArrayIndex(Object obj, int idx);

    Object getMapValue(Object obj, String key);

    void setProperty(Object obj, Object key, Object value);

    boolean isMap(Object obj);
}
