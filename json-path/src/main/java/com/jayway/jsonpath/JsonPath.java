package com.jayway.jsonpath;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

import com.jayway.jsonpath.internal.IOUtils;
import com.jayway.jsonpath.internal.PathToken;
import com.jayway.jsonpath.internal.PathTokenizer;
import com.jayway.jsonpath.internal.filter.PathTokenFilter;
import com.jayway.jsonpath.spi.HttpProviderFactory;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

public class JsonPath {

    private static Pattern DEFINITE_PATH_PATTERN =
            Pattern.compile(".*(\\.\\.|\\*|\\[[\\\\/]|\\?|,|:\\s?\\]|\\[\\s?:|>|\\(|<|=|\\+).*");

    private PathTokenizer tokenizer;
    private LinkedList<Filter> filters;

    public JsonPath(String jsonPath, Filter[] filters) {
        if (jsonPath == null
                || jsonPath.trim().length() == 0
                || jsonPath.matches("[^\\?\\+\\=\\-\\*\\/\\!]\\(")) {

            throw new InvalidPathException("Invalid path");
        }

        int filterCountInPath = StringUtils.countMatches(jsonPath, "[?]");
        isTrue(
                filterCountInPath == filters.length,
                "Filters in path ([?]) does not match provided filters.");

        this.tokenizer = new PathTokenizer(jsonPath);
        this.filters = new LinkedList<Filter>();
        this.filters.addAll(asList(filters));
    }

    PathTokenizer getTokenizer() {
        return this.tokenizer;
    }

    public JsonPath copy() {
        return new JsonPath(tokenizer.getPath(), filters.toArray(new Filter[0]));
    }

    public String getPath() {
        return this.tokenizer.getPath();
    }

    public static boolean isPathDefinite(String path) {
        String preparedPath = path.replaceAll("\"[^\"\\\\\\n\r]*\"", "");

        return !DEFINITE_PATH_PATTERN.matcher(preparedPath).matches();
    }

    public boolean isPathDefinite() {
        return JsonPath.isPathDefinite(getPath());
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(Object jsonObject) {
        notNull(jsonObject, "json can not be null");

        JsonProvider jsonProvider = JsonProviderFactory.createProvider();

        if (!jsonProvider.isMap(jsonObject) && !jsonProvider.isList(jsonObject)) {
            throw new IllegalArgumentException("Invalid container object");
        }
        LinkedList<Filter> contextFilters = new LinkedList<Filter>(filters);

        Object result = jsonObject;

        boolean inArrayContext = false;

        for (PathToken pathToken : tokenizer) {
            PathTokenFilter filter = pathToken.getFilter();
            result = filter.filter(result, jsonProvider, contextFilters, inArrayContext);

            if (!inArrayContext) {
                inArrayContext = filter.isArrayFilter();
            }
        }
        return (T) result;
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(String json) {
        notEmpty(json, "json can not be null or empty");

        return (T) read(JsonProviderFactory.createProvider().parse(json));
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(URL jsonURL) throws IOException {
        notNull(jsonURL, "json URL can not be null");

        InputStream in = null;
        try {
            in = HttpProviderFactory.getProvider().get(jsonURL);
            return (T) read(JsonProviderFactory.createProvider().parse(in));
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(File jsonFile) throws IOException {
        notNull(jsonFile, "json file can not be null");
        isTrue(jsonFile.exists(), "json file does not exist");

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(jsonFile);
            return (T) read(JsonProviderFactory.createProvider().parse(fis));
        } finally {
            IOUtils.closeQuietly(fis);
        }
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(InputStream jsonInputStream) throws IOException {
        notNull(jsonInputStream, "json input stream can not be null");

        try {
            return (T) read(JsonProviderFactory.createProvider().parse(jsonInputStream));
        } finally {
            IOUtils.closeQuietly(jsonInputStream);
        }
    }

    public static JsonPath compile(String jsonPath, Filter... filters) {
        notEmpty(jsonPath, "json can not be null or empty");

        return new JsonPath(jsonPath, filters);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T read(String json, String jsonPath, Filter... filters) {
        notEmpty(json, "json can not be null or empty");
        notEmpty(jsonPath, "jsonPath can not be null or empty");

        return (T) compile(jsonPath, filters).read(json);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T read(Object json, String jsonPath, Filter... filters) {
        notNull(json, "json can not be null");
        notNull(jsonPath, "jsonPath can not be null");

        return (T) compile(jsonPath, filters).read(json);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T read(URL jsonURL, String jsonPath, Filter... filters) throws IOException {
        notNull(jsonURL, "json URL can not be null");
        notEmpty(jsonPath, "jsonPath can not be null or empty");

        return (T) compile(jsonPath, filters).read(jsonURL);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T read(File jsonFile, String jsonPath, Filter... filters) throws IOException {
        notNull(jsonFile, "json file can not be null");
        notEmpty(jsonPath, "jsonPath can not be null or empty");

        return (T) compile(jsonPath, filters).read(jsonFile);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T read(InputStream jsonInputStream, String jsonPath, Filter... filters)
            throws IOException {
        notNull(jsonInputStream, "json input stream can not be null");
        notEmpty(jsonPath, "jsonPath can not be null or empty");

        return (T) compile(jsonPath, filters).read(jsonInputStream);
    }
}
