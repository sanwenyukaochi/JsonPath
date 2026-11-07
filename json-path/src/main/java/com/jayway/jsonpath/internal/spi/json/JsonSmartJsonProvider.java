package com.jayway.jsonpath.internal.spi.json;

import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.spi.json.Mode;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import net.minidev.json.writer.JsonReaderI;

public class JsonSmartJsonProvider extends AbstractJsonProvider {

    private final Mode mode;

    private static final JsonReaderI<?> orderedMapper = JSONValue.defaultReader.DEFAULT_ORDERED;

    public JsonSmartJsonProvider() {
        this(Mode.SLACK);
    }

    public JsonSmartJsonProvider(Mode mode) {
        this.mode = mode;
    }

    public Object createMap() {
        return orderedMapper.createObject();
    }

    public Object createArray() {
        return orderedMapper.createArray();
    }

    public Object parse(String json) {
        try {
            return createParser().parse(json, orderedMapper);
        } catch (ParseException e) {
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public Object parse(Reader jsonReader) throws InvalidJsonException {
        try {
            return createParser().parse(jsonReader, orderedMapper);
        } catch (ParseException e) {
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public Object parse(InputStream jsonStream) throws InvalidJsonException {
        try {
            return createParser().parse(new InputStreamReader(jsonStream), orderedMapper);
        } catch (ParseException e) {
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public String toJson(Object obj) {

        if (obj instanceof Map) {
            return JSONObject.toJSONString((Map<String, ?>) obj);
        } else if (obj instanceof List) {
            return JSONArray.toJSONString((List<?>) obj);
        } else {
            throw new UnsupportedOperationException(
                    obj.getClass().getName() + " can not be converted to JSON");
        }
    }

    private JSONParser createParser() {
        return new JSONParser(mode.intValue());
    }
}
