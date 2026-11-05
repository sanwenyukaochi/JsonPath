package com.jayway.jsonpath;

import com.jayway.jsonpath.filter.FilterOutput;
import com.jayway.jsonpath.filter.JsonPathFilterChain;
import java.util.logging.Logger;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public class JsonPath {

    public static final int STRICT_MODE = 0;
    public static final int SLACK_MODE = -1;

    private static int mode = SLACK_MODE;

    private static final Logger log = Logger.getLogger(JsonPath.class.getName());

    private static JSONParser JSON_PARSER = new JSONParser(JsonPath.mode);

    private JsonPathFilterChain filters;

    public static void setMode(int mode) {
        if (mode != JsonPath.mode) {
            JsonPath.mode = mode;
            JSON_PARSER = new JSONParser(JsonPath.mode);
        }
    }

    public static int getMode() {
        return mode;
    }

    private JsonPath(String jsonPath) {
        if (jsonPath == null
                || jsonPath.trim().isEmpty()
                || jsonPath.matches("new ")
                || jsonPath.matches("[^\\?\\+\\=\\-\\*\\/\\!]\\(")) {

            throw new InvalidPathException("Invalid path");
        }
        this.filters = new JsonPathFilterChain(PathUtil.splitPath(jsonPath));
    }

    public <T> T read(Object json) {
        FilterOutput filterOutput = filters.filter(json);

        if (filterOutput == null || filterOutput.getResult() == null) {
            return null;
        }

        return (T) filterOutput.getResult();
    }

    public <T> T read(String json) throws java.text.ParseException {
        return (T) read(parse(json));
    }

    public static JsonPath compile(String jsonPath) {
        return new JsonPath(jsonPath);
    }

    public static <T> T read(String json, String jsonPath) throws java.text.ParseException {
        return (T) compile(jsonPath).read(json);
    }

    public static <T> T read(Object json, String jsonPath) {
        return (T) compile(jsonPath).read(json);
    }

    private static Object parse(String json) throws java.text.ParseException {
        try {
            return JSON_PARSER.parse(json);
        } catch (ParseException e) {
            throw new java.text.ParseException(json, e.getPosition());
        }
    }
}
