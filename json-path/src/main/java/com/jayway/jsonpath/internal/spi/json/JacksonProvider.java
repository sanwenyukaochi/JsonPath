package com.jayway.jsonpath.internal.spi.json;

import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.spi.json.Mode;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;

public class JacksonProvider extends AbstractJsonProvider {

    private static final Logger logger = LoggerFactory.getLogger(JacksonProvider.class);

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
            logger.debug("Invalid JSON: \n" + json);
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
}
