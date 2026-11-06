package com.jayway.jsonpath;

import com.jayway.jsonpath.reader.PathToken;
import com.jayway.jsonpath.reader.PathTokenizer;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public class JsonPath {

    public static final int STRICT_MODE = 0;
    public static final int SLACK_MODE = -1;

    private static int mode = SLACK_MODE;

    private static final Logger log = Logger.getLogger(JsonPath.class.getName());

    private static JSONParser JSON_PARSER = new JSONParser(JsonPath.mode);

    private static Pattern DEFINITE_PATH_PATTERN =
            Pattern.compile(".*(\\.\\.|\\*|\\[[\\\\/]|\\?|,|:\\s?\\]|\\[\\s?:|>|\\(|<|=|\\+).*");

    private PathTokenizer tokenizer;

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
        this.tokenizer = new PathTokenizer(jsonPath);
    }

    public String getPath() {
        return this.tokenizer.getPath();
    }

    public boolean isPathDefinite() {

        String preparedPath = getPath().replaceAll("\"[^\"\\\\\\n\r]*\"", "");

        return !DEFINITE_PATH_PATTERN.matcher(preparedPath).matches();
    }

    public <T> T read(Object container) {

        if (!(container instanceof Map) && !(container instanceof List)) {
            throw new IllegalArgumentException("Invalid container object");
        }

        Object result = container;

        for (PathToken pathToken : tokenizer) {
            result = pathToken.filter(result);
        }
        return (T) result;
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

    public static Object parse(String json) throws java.text.ParseException {
        try {
            return JSON_PARSER.parse(json);
        } catch (ParseException e) {
            throw new java.text.ParseException(json, e.getPosition());
        }
    }
}
