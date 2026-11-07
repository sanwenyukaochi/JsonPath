package com.jayway.jsonpath;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import com.jayway.jsonpath.internal.IOUtils;
import com.jayway.jsonpath.internal.JsonReader;
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

    private PathTokenizer tokenizer;
    private LinkedList<Filter> filters;

    private JsonPath(String jsonPath, Filter[] filters) {

        notNull(jsonPath, "path can not be null");
        jsonPath = jsonPath.trim();
        notEmpty(jsonPath, "path can not be empty");

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
        Filter[] filterCopy = filters.isEmpty() ? new Filter[0] : new Filter[filters.size()];
        return new JsonPath(tokenizer.getPath(), filters.toArray(filterCopy));
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
        return read(jsonObject, Configuration.defaultConfiguration());
    }

    public <T> T read(Object jsonObject, Configuration configuration) {
        notNull(jsonObject, "json can not be null");
        notNull(configuration, "configuration can not be null");

        if (this.getPath().equals("$")) {

            return (T) jsonObject;
        }

        if (!configuration.getProvider().isContainer(jsonObject)) {
            throw new IllegalArgumentException("Invalid container object");
        }

        LinkedList<Filter> contextFilters = new LinkedList<Filter>(filters);

        Object result = jsonObject;

        boolean inArrayContext = false;

        for (PathToken pathToken : tokenizer) {
            PathTokenFilter filter = pathToken.getFilter();

            if (LOG.isDebugEnabled()) {
                LOG.debug("Applying filter: " + filter + " to " + result);
            }

            result = filter.filter(result, configuration, contextFilters, inArrayContext);

            if (result == null && !pathToken.isEndToken()) {
                throw new PathNotFoundException(
                        "Path token: '" + pathToken.getFragment() + "' not found.");
            }

            if (!inArrayContext) {
                inArrayContext = filter.isArrayFilter();
            }
        }
        return (T) result;
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(String json) {
        return read(json, Configuration.defaultConfiguration());
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(String json, Configuration configuration) {
        notEmpty(json, "json can not be null or empty");
        notNull(configuration, "jsonProvider can not be null");

        return read(configuration.getProvider().parse(json), configuration);
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(URL jsonURL) throws IOException {
        return read(jsonURL, Configuration.defaultConfiguration());
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(URL jsonURL, Configuration configuration) throws IOException {
        notNull(jsonURL, "json URL can not be null");
        notNull(configuration, "jsonProvider can not be null");

        InputStream in = null;
        try {
            in = HttpProviderFactory.getProvider().get(jsonURL);
            return read(configuration.getProvider().parse(in), configuration);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(File jsonFile) throws IOException {
        return read(jsonFile, Configuration.defaultConfiguration());
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(File jsonFile, Configuration configuration) throws IOException {
        notNull(jsonFile, "json file can not be null");
        isTrue(jsonFile.exists(), "json file does not exist");
        notNull(configuration, "jsonProvider can not be null");

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(jsonFile);
            return read(configuration.getProvider().parse(fis), configuration);
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
    public <T> T read(InputStream jsonInputStream, Configuration configuration) throws IOException {
        notNull(jsonInputStream, "json input stream can not be null");
        notNull(configuration, "configuration can not be null");

        try {
            return read(configuration.getProvider().parse(jsonInputStream), configuration);
        } finally {
            IOUtils.closeQuietly(jsonInputStream);
        }
    }

    public static JsonPath compile(String jsonPath, Filter... filters) {
        notEmpty(jsonPath, "json can not be null or empty");

        return new JsonPath(jsonPath, filters);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T read(Object json, String jsonPath, Filter... filters) {
        return compile(jsonPath, filters).read(json);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T read(String json, String jsonPath, Filter... filters) {
        return new JsonReader().parse(json).read(jsonPath, filters);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T read(URL jsonURL, String jsonPath, Filter... filters) throws IOException {
        return new JsonReader().parse(jsonURL).read(jsonPath, filters);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T read(File jsonFile, String jsonPath, Filter... filters) throws IOException {
        return new JsonReader().parse(jsonFile).read(jsonPath, filters);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T read(InputStream jsonInputStream, String jsonPath, Filter... filters)
            throws IOException {
        return new JsonReader().parse(jsonInputStream).read(jsonPath, filters);
    }

    public static ParseContext using(Configuration configuration) {
        return new JsonReader(configuration);
    }

    public static ParseContext using(JsonProvider provider) {
        return new JsonReader(Configuration.builder().jsonProvider(provider).build());
    }

    public static ReadContext parse(Object json) {
        return new JsonReader().parse(json);
    }

    public static ReadContext parse(String json) {
        return new JsonReader().parse(json);
    }

    public static ReadContext parse(InputStream json) {
        return new JsonReader().parse(json);
    }

    public static ReadContext parse(File json) throws IOException {
        return new JsonReader().parse(json);
    }

    public static ReadContext parse(URL json) throws IOException {
        return new JsonReader().parse(json);
    }

    public static ReadContext parse(Object json, Configuration configuration) {
        return new JsonReader(configuration).parse(json);
    }

    public static ReadContext parse(String json, Configuration configuration) {
        return new JsonReader(configuration).parse(json);
    }

    public static ReadContext parse(InputStream json, Configuration configuration) {
        return new JsonReader(configuration).parse(json);
    }

    public static ReadContext parse(File json, Configuration configuration) throws IOException {
        return new JsonReader(configuration).parse(json);
    }

    public static ReadContext parse(URL json, Configuration configuration) throws IOException {
        return new JsonReader(configuration).parse(json);
    }
}
