package com.jayway.jsonpath.spi.json;

import com.jayway.jsonpath.InvalidJsonException;

import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;

public interface JsonProvider {

    static final Object UNDEFINED = new Object();

    Object parse(String json) throws InvalidJsonException;

    Object parse(Reader jsonReader) throws InvalidJsonException;

    Object parse(InputStream jsonStream) throws InvalidJsonException;

    String toJson(Object obj);

    Object createMap();

    Object createArray();

    boolean isArray(Object obj);

    int length(Object obj);

    Iterable<Object> toIterable(Object obj);

    Collection<String> getPropertyKeys(Object obj);

    Object getArrayIndex(Object obj, int idx);

    Object getMapValue(Object obj, String key);

    Object getMapValue(Object obj, String key, boolean signalUndefined);

    void setProperty(Object obj, Object key, Object value);

    boolean isMap(Object obj);
}
