package com.jayway.jsonpath;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonPath {

    private static final Logger LOG = LoggerFactory.getLogger(JsonPath.class.getName());

    private static Pattern DEFINITE_PATH_PATTERN =
            Pattern.compile(".*(\\.\\.|\\*|\\[[\\\\/]|\\?|,|:\\s?]|\\[\\s?:|>|\\(|<|=|\\+).*");
    private static Pattern INVALID_PATH_PATTERN = Pattern.compile("[^\\?\\+=\\-\\*/!]\\(");

    private PathTokenizer tokenizer;
    private LinkedList<Filter> filters;

    private JsonPath(String jsonPath, Filter[] filters) {

        notNull(jsonPath, "path can not be null");
        jsonPath = jsonPath.trim();
        notEmpty(jsonPath, "path can not be empty");

        if (INVALID_PATH_PATTERN.matcher(jsonPath).matches()) {
            throw new InvalidPathException("Invalid path");
        }

        int filterCountInPath = StringUtils.countMatches(jsonPath, "[?]");
        isTrue(
                filterCountInPath == filters.length,
                "Filters in path ([?]) does not match provided filters.");

        this.tokenizer = new PathTokenizer(jsonPath);

        if (LOG.isDebugEnabled()) {
            LOG.debug("New JsonPath:\n{}", this.tokenizer.toString());
        }

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
        return read(JsonProviderFactory.createProvider(), jsonObject);
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(JsonProvider jsonProvider, Object jsonObject) {
        notNull(jsonProvider, "jsonProvider can not be null");
        notNull(jsonObject, "json can not be null");

        if (this.getPath().equals("$")) {

            return (T) jsonObject;
        }

        if (!jsonProvider.isContainer(jsonObject)) {
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
        return read(JsonProviderFactory.createProvider(), json);
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(JsonProvider jsonProvider, String json) {
        notNull(jsonProvider, "jsonProvider can not be null");
        notEmpty(json, "json can not be null or empty");

        return read(jsonProvider, jsonProvider.parse(json));
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(URL jsonURL) throws IOException {
        return read(JsonProviderFactory.createProvider(), jsonURL);
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(JsonProvider jsonProvider, URL jsonURL) throws IOException {
        notNull(jsonProvider, "jsonProvider can not be null");
        notNull(jsonURL, "json URL can not be null");

        InputStream in = null;
        try {
            in = HttpProviderFactory.getProvider().get(jsonURL);
            return read(jsonProvider, jsonProvider.parse(in));
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(File jsonFile) throws IOException {
        return read(JsonProviderFactory.createProvider(), jsonFile);
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(JsonProvider jsonProvider, File jsonFile) throws IOException {
        notNull(jsonProvider, "jsonProvider can not be null");
        notNull(jsonFile, "json file can not be null");
        isTrue(jsonFile.exists(), "json file does not exist");

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(jsonFile);
            return read(jsonProvider, jsonProvider.parse(fis));
        } finally {
            IOUtils.closeQuietly(fis);
        }
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(InputStream jsonInputStream) throws IOException {
        notNull(jsonInputStream, "json input stream can not be null");

        try {
            return read(JsonProviderFactory.createProvider().parse(jsonInputStream));
        } finally {
            IOUtils.closeQuietly(jsonInputStream);
        }
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(JsonProvider jsonProvider, InputStream jsonInputStream) throws IOException {
        notNull(jsonProvider, "jsonProvider can not be null");
        notNull(jsonInputStream, "json input stream can not be null");

        try {
            return read(jsonProvider, jsonProvider.parse(jsonInputStream));
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
        return read(JsonProviderFactory.createProvider(), json, jsonPath, filters);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T read(
            JsonProvider jsonProvider, String json, String jsonPath, Filter... filters) {
        notNull(jsonProvider, "jsonProvider can not be null");
        notEmpty(json, "json can not be null or empty");
        notEmpty(jsonPath, "jsonPath can not be null or empty");

        return compile(jsonPath, filters).read(jsonProvider, json);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T read(Object json, String jsonPath, Filter... filters) {
        return read(JsonProviderFactory.createProvider(), json, jsonPath, filters);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T read(
            JsonProvider jsonProvider, Object json, String jsonPath, Filter... filters) {
        notNull(jsonProvider, "jsonProvider can not be null");
        notNull(json, "json can not be null");
        notNull(jsonPath, "jsonPath can not be null");

        return compile(jsonPath, filters).read(jsonProvider, json);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T read(URL jsonURL, String jsonPath, Filter... filters) throws IOException {
        return read(JsonProviderFactory.createProvider(), jsonURL, jsonPath, filters);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T read(
            JsonProvider jsonProvider, URL jsonURL, String jsonPath, Filter... filters)
            throws IOException {
        notNull(jsonProvider, "jsonProvider can not be null");
        notNull(jsonURL, "json URL can not be null");
        notEmpty(jsonPath, "jsonPath can not be null or empty");

        return compile(jsonPath, filters).read(jsonProvider, jsonURL);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T read(File jsonFile, String jsonPath, Filter... filters) throws IOException {
        return read(JsonProviderFactory.createProvider(), jsonFile, jsonPath, filters);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T read(
            JsonProvider jsonProvider, File jsonFile, String jsonPath, Filter... filters)
            throws IOException {
        notNull(jsonProvider, "jsonProvider can not be null");
        notNull(jsonFile, "json file can not be null");
        notEmpty(jsonPath, "jsonPath can not be null or empty");

        return compile(jsonPath, filters).read(jsonProvider, jsonFile);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T read(InputStream jsonInputStream, String jsonPath, Filter... filters)
            throws IOException {
        return read(JsonProviderFactory.createProvider(), jsonInputStream, jsonPath, filters);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T read(
            JsonProvider jsonProvider,
            InputStream jsonInputStream,
            String jsonPath,
            Filter... filters)
            throws IOException {
        notNull(jsonProvider, "jsonProvider can not be null");
        notNull(jsonInputStream, "json input stream can not be null");
        notEmpty(jsonPath, "jsonPath can not be null or empty");

        return compile(jsonPath, filters).read(jsonProvider, jsonInputStream);
    }
}
