package com.jayway.jsonpath;

import com.jayway.jsonpath.internal.PathToken;
import com.jayway.jsonpath.internal.PathTokenizer;
import com.jayway.jsonpath.internal.filter.Filter;
import com.jayway.jsonpath.spi.JsonProviderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;

public class JsonPath {

    private static Pattern DEFINITE_PATH_PATTERN =
            Pattern.compile(".*(\\.\\.|\\*|\\[[\\\\/]|\\?|,|:\\s?\\]|\\[\\s?:|>|\\(|<|=|\\+).*");

    private PathTokenizer tokenizer;

    private JsonPath(String jsonPath) {
        if (jsonPath == null
                || jsonPath.trim().isEmpty()
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

    @SuppressWarnings({"unchecked"})
    public <T> T read(Object json) {
        if (!(json instanceof Map) && !(json instanceof List)) {
            throw new IllegalArgumentException("Invalid container object");
        }

        Object result = json;

        boolean inArrayContext = false;

        for (PathToken pathToken : tokenizer) {
            Filter filter = pathToken.getFilter();
            result = filter.filter(result, JsonProviderFactory.getInstance(), inArrayContext);

            if (!inArrayContext) {
                inArrayContext = filter.isArrayFilter();
            }
        }
        return (T) result;
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(String json) {
        return (T) read(JsonProviderFactory.getInstance().parse(json));
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(URL jsonURL) throws IOException {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(jsonURL.openStream()));
            return (T) read(JsonProviderFactory.getInstance().parse(in));
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(File jsonFile) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(jsonFile);
            return (T) read(JsonProviderFactory.getInstance().parse(fis));
        } finally {
            IOUtils.closeQuietly(fis);
        }
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(InputStream jsonInputStream) throws IOException {
        try {
            return (T) read(JsonProviderFactory.getInstance().parse(jsonInputStream));
        } finally {
            IOUtils.closeQuietly(jsonInputStream);
        }
    }

    public static JsonPath compile(String jsonPath) {
        return new JsonPath(jsonPath);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T read(String json, String jsonPath) {
        return (T) compile(jsonPath).read(json);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T read(Object json, String jsonPath) {
        return (T) compile(jsonPath).read(json);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T read(URL jsonURL, String jsonPath) throws IOException {
        return (T) compile(jsonPath).read(jsonURL);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T read(File jsonFile, String jsonPath) throws IOException {
        return (T) compile(jsonPath).read(jsonFile);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T read(InputStream jsonInputStream, String jsonPath) throws IOException {
        return (T) compile(jsonPath).read(jsonInputStream);
    }
}
