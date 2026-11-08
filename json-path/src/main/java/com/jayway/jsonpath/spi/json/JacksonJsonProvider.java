package com.jayway.jsonpath.spi.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.jayway.jsonpath.InvalidJsonException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class JacksonJsonProvider extends AbstractJsonProvider {

    private static final ObjectMapper defaultObjectMapper = new ObjectMapper();
    private static final ObjectReader defaultObjectReader =
            defaultObjectMapper.reader().forType(Object.class);

    protected ObjectMapper objectMapper;
    protected ObjectReader objectReader;

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public JacksonJsonProvider() {
        this(defaultObjectMapper, defaultObjectReader);
    }

    public JacksonJsonProvider(ObjectMapper objectMapper) {
        this(objectMapper, objectMapper.reader().forType(Object.class));
    }

    public JacksonJsonProvider(ObjectMapper objectMapper, ObjectReader objectReader) {
        this.objectMapper = objectMapper;
        this.objectReader = objectReader;
    }

    @Override
    public Object parse(String json) throws InvalidJsonException {
        try {
            return objectReader.readValue(json);
        } catch (IOException e) {
            throw new InvalidJsonException(e, json);
        }
    }

    @Override
    public Object parse(byte[] json) throws InvalidJsonException {
        try {
            return objectReader.readValue(json);
        } catch (IOException e) {
            throw new InvalidJsonException(e, new String(json, StandardCharsets.UTF_8));
        }
    }

    @Override
    public Object parse(InputStream jsonStream, String charset) throws InvalidJsonException {
        try {
            return objectReader.readValue(new InputStreamReader(jsonStream, charset));
        } catch (IOException e) {
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public String toJson(Object obj) {
        StringWriter writer = new StringWriter();
        try {
            JsonGenerator generator = objectMapper.getFactory().createGenerator(writer);
            objectMapper.writeValue(generator, obj);
            writer.flush();
            writer.close();
            generator.close();
            return writer.getBuffer().toString();
        } catch (IOException e) {
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public List<Object> createArray() {
        return new LinkedList<Object>();
    }

    @Override
    public Object createMap() {
        return new LinkedHashMap<String, Object>();
    }
}
