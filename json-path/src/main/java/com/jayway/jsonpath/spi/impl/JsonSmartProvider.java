package com.jayway.jsonpath.spi.impl;

import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.spi.Mode;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public class JsonSmartProvider extends AbstractJsonProvider {

    private Mode mode;

    private JSONParser parser;

    public JsonSmartProvider() {
        this(Mode.SLACK);
    }

    public JsonSmartProvider(Mode mode) {
        this.mode = mode;
        this.parser = new JSONParser(mode.intValue());
    }

    public Map<String, Object> createMap() {
        return new JSONObject();
    }

    public List<Object> createList() {
        return new JSONArray();
    }

    public Object parse(String json) {
        try {
            return parser.parse(json);
        } catch (ParseException e) {
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public Object parse(Reader jsonReader) throws InvalidJsonException {
        try {
            return parser.parse(jsonReader);
        } catch (ParseException e) {
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public Object parse(InputStream jsonStream) throws InvalidJsonException {
        try {
            return parser.parse(new InputStreamReader(jsonStream));
        } catch (ParseException e) {
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public String toJson(Object obj) {

        if (obj instanceof Map) {
            return JSONObject.toJSONString((Map<String, ? extends Object>) obj);
        } else if (obj instanceof List) {
            return JSONArray.toJSONString((List<? extends Object>) obj);
        } else {
            throw new UnsupportedOperationException(
                    obj.getClass().getName() + " can not be converted to JSON");
        }
    }

    public Mode getMode() {
        return mode;
    }
}
