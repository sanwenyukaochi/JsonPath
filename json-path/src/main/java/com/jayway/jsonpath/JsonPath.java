package com.jayway.jsonpath;

import com.jayway.jsonpath.reader.PathToken;
import com.jayway.jsonpath.reader.PathTokenizer;
import com.jayway.jsonpath.spi.JsonProvider;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class JsonPath {

    private static Pattern DEFINITE_PATH_PATTERN =
            Pattern.compile(".*(\\.\\.|\\*|\\[[\\\\/]|\\?|,|:\\s?\\]|\\[\\s?:|>|\\(|<|=|\\+).*");

    private PathTokenizer tokenizer;

    private JsonProvider jsonProvider;

    private JsonPath(String jsonPath) {
        this(JsonProvider.getInstance(), jsonPath);
    }

    private JsonPath(JsonProvider jsonProvider, String jsonPath) {
        if (jsonPath == null
                || jsonPath.trim().isEmpty()
                || jsonPath.matches("new ")
                || jsonPath.matches("[^\\?\\+\\=\\-\\*\\/\\!]\\(")) {

            throw new InvalidPathException("Invalid path");
        }
        this.jsonProvider = jsonProvider;
        this.tokenizer = new PathTokenizer(jsonPath, jsonProvider);
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
            result = pathToken.filter(result, jsonProvider);
        }
        return (T) result;
    }

    public <T> T read(String json) {
        return (T) read(jsonProvider.parse(json));
    }

    public static JsonPath compile(String jsonPath) {
        return new JsonPath(jsonPath);
    }

    public static JsonPath compile(JsonProvider provider, String jsonPath) {
        return new JsonPath(provider, jsonPath);
    }

    public static <T> T read(String json, String jsonPath) {
        return (T) compile(jsonPath).read(json);
    }

    public static <T> T read(Object json, String jsonPath) {
        return (T) compile(jsonPath).read(json);
    }
}
