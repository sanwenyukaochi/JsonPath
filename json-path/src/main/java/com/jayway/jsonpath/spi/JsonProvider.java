package com.jayway.jsonpath.spi;

import com.jayway.jsonpath.InvalidJsonException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;

public interface JsonProvider {

    Mode getMode();

    Object parse(String json) throws InvalidJsonException;

    Object parse(Reader jsonReader) throws InvalidJsonException;

    Object parse(InputStream jsonStream) throws InvalidJsonException;

    String toJson(Object obj);

    Object createMap();

    Iterable createArray();

    Object clone(Object model);

    boolean isContainer(Object obj);

    boolean isArray(Object obj);

    int length(Object obj);

    Iterable<Object> toIterable(Object obj);

    Collection<String> getPropertyKeys(Object obj);

    Object getProperty(Object obj, Object key);

    void setProperty(Object obj, Object key, Object value);

    boolean isMap(Object obj);
}
