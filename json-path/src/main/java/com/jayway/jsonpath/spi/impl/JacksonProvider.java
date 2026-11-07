package com.jayway.jsonpath.spi.impl;

import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.spi.MappingProvider;
import com.jayway.jsonpath.spi.Mode;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.type.CollectionType;

public class JacksonProvider extends AbstractJsonProvider implements MappingProvider {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mode getMode() {
        return Mode.STRICT;
    }

    @Override
    public Object parse(String json) throws InvalidJsonException {
        try {
            return objectMapper.readValue(json, Object.class);
        } catch (JacksonException e) {
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public Object parse(Reader jsonReader) throws InvalidJsonException {
        try {
            return objectMapper.readValue(jsonReader, Object.class);
        } catch (JacksonException e) {
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public Object parse(InputStream jsonStream) throws InvalidJsonException {
        try {
            return objectMapper.readValue(jsonStream, Object.class);
        } catch (JacksonException e) {
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public String toJson(Object obj) {
        StringWriter writer = new StringWriter();
        try {
            JsonGenerator jsonGenerator = objectMapper.createGenerator(writer);
            objectMapper.writeValue(jsonGenerator, obj);
            writer.close();
            return writer.getBuffer().toString();
        } catch (IOException | JacksonException e) {
            throw new InvalidJsonException();
        }
    }

    @Override
    public Map<String, Object> createMap() {
        return new HashMap<String, Object>();
    }

    @Override
    public List<Object> createArray() {
        return new LinkedList<Object>();
    }

    @Override
    public <T> T convertValue(Object fromValue, Class<T> toValueType)
            throws IllegalArgumentException {
        return objectMapper.convertValue(fromValue, toValueType);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T extends Collection<E>, E> T convertValue(
            Object fromValue, Class<T> collectionType, Class<E> elementType)
            throws IllegalArgumentException {
        CollectionType colType =
                objectMapper.getTypeFactory().constructCollectionType(collectionType, elementType);

        return (T) objectMapper.convertValue(fromValue, colType);
    }
}
