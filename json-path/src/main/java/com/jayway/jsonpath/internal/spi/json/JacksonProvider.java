package com.jayway.jsonpath.internal.spi.json;

import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.spi.json.Mode;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;

public class JacksonProvider extends AbstractJsonProvider {

    private static final Logger logger = LoggerFactory.getLogger(JacksonProvider.class);

    private static final ObjectMapper defaultObjectMapper = new ObjectMapper();
    private static final ObjectReader defaultObjectReader =
            defaultObjectMapper.reader().forType(Object.class);

    protected ObjectMapper objectMapper;
    protected ObjectReader objectReader;

    public JacksonProvider() {
        this(defaultObjectMapper, defaultObjectReader);
    }

    public JacksonProvider(ObjectMapper objectMapper) {
        this(objectMapper, objectMapper.reader().forType(Object.class));
    }

    public JacksonProvider(ObjectMapper objectMapper, ObjectReader objectReader) {
        this.objectMapper = objectMapper;
        this.objectReader = objectReader;
    }

    public Mode getMode() {
        return Mode.STRICT;
    }

    @Override
    public Object parse(String json) throws InvalidJsonException {
        try {
            return objectReader.readValue(json);
        } catch (JacksonException e) {
            logger.debug("Invalid JSON: \n" + json);
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public Object parse(Reader jsonReader) throws InvalidJsonException {
        try {
            return objectReader.readValue(jsonReader);
        } catch (JacksonException e) {
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public Object parse(InputStream jsonStream) throws InvalidJsonException {
        try {
            return objectReader.readValue(jsonStream);
        } catch (JacksonException e) {
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public String toJson(Object obj) {
        StringWriter writer = new StringWriter();
        try {
            JsonGenerator generator = objectMapper.createGenerator(writer);
            objectMapper.writeValue(generator, obj);
            writer.flush();
            writer.close();
            generator.close();
            return writer.getBuffer().toString();
        } catch (IOException | JacksonException e) {
            throw new InvalidJsonException();
        }
    }

    @Override
    public Map<String, Object> createMap() {
        return new LinkedHashMap<String, Object>();
    }

    @Override
    public List<Object> createArray() {
        return new LinkedList<Object>();
    }
}
