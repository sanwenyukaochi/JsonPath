package com.jayway.jsonpath.spi;

import com.jayway.jsonpath.InvalidJsonException;

import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Map;

public interface JsonProvider {

    Mode getMode();

    Object parse(String json) throws InvalidJsonException;

    Object parse(Reader jsonReader) throws InvalidJsonException;

    Object parse(InputStream jsonStream) throws InvalidJsonException;

    String toJson(Object obj);

    Map<String, Object> createMap();

    List<Object> createList();

    Object clone(Object model);

    boolean isContainer(Object obj);

    boolean isList(Object obj);

    List<Object> toList(Object list);

    Map<String, Object> toMap(Object map);

    Object getMapValue(Object map, String key);

    boolean isMap(Object obj);
}
