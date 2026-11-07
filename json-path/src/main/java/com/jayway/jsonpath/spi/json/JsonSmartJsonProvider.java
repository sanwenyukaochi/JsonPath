package com.jayway.jsonpath.spi.json;

import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.JsonPathException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import net.minidev.json.writer.JsonReaderI;

public class JsonSmartJsonProvider extends AbstractJsonProvider {

    private final int parseMode;
    private final JsonReaderI<?> mapper;

    public JsonSmartJsonProvider() {
        this(JSONParser.MODE_PERMISSIVE, JSONValue.defaultReader.DEFAULT_ORDERED);
    }

    public JsonSmartJsonProvider(int parseMode) {
        this(parseMode, JSONValue.defaultReader.DEFAULT_ORDERED);
    }

    public JsonSmartJsonProvider(int parseMode, JsonReaderI<?> mapper) {
        this.parseMode = parseMode;
        this.mapper = mapper;
    }

    public Object createArray() {
        return mapper.createArray();
    }

    public Object createMap() {
        return mapper.createObject();
    }

    public Object parse(String json) {
        try {
            return createParser().parse(json, mapper);
        } catch (ParseException e) {
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public Object parse(InputStream jsonStream, String charset) throws InvalidJsonException {
        try {
            return createParser().parse(new InputStreamReader(jsonStream, charset), mapper);
        } catch (ParseException e) {
            throw new InvalidJsonException(e);
        } catch (UnsupportedEncodingException e) {
            throw new JsonPathException(e);
        }
    }

    @Override
    public String toJson(Object obj) {

        if (obj instanceof Map) {
            return JSONObject.toJSONString((Map<String, ?>) obj, JSONStyle.LT_COMPRESS);
        } else if (obj instanceof List) {
            return JSONArray.toJSONString((List<?>) obj, JSONStyle.LT_COMPRESS);
        } else {
            throw new UnsupportedOperationException(
                    obj.getClass().getName() + " can not be converted to JSON");
        }
    }

    private JSONParser createParser() {
        return new JSONParser(parseMode);
    }
}
