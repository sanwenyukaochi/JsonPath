package com.jayway.jsonpath;

import static com.jayway.jsonpath.internal.Utils.isTrue;
import static com.jayway.jsonpath.internal.Utils.notEmpty;
import static com.jayway.jsonpath.internal.Utils.notNull;

import com.jayway.jsonpath.internal.JsonReader;
import com.jayway.jsonpath.internal.Utils;
import com.jayway.jsonpath.internal.spi.compiler.PathCompiler;
import com.jayway.jsonpath.spi.compiler.Path;
import com.jayway.jsonpath.spi.http.HttpProviderFactory;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.json.JsonProviderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class JsonPath {

    private final Path path;

    private JsonPath(String jsonPath, Filter[] filters) {
        notNull(jsonPath, "path can not be null");
        this.path = PathCompiler.tokenize(jsonPath, filters);
    }

    public String getPath() {
        return this.path.toString();
    }

    public static boolean isPathDefinite(String path) {
        return compile(path).isDefinite();
    }

    public boolean isDefinite() {
        return path.isDefinite();
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(Object jsonObject) {
        return read(jsonObject, Configuration.defaultConfiguration());
    }

    public <T> T read(Object jsonObject, Configuration configuration) {
        return (T) path.evaluate(jsonObject, configuration).getWithOptions();
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
            Utils.closeQuietly(in);
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
            Utils.closeQuietly(fis);
        }
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(InputStream jsonInputStream) throws IOException {
        notNull(jsonInputStream, "json input stream can not be null");

        try {
            return read(JsonProviderFactory.createProvider().parse(jsonInputStream));
        } finally {
            Utils.closeQuietly(jsonInputStream);
        }
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(InputStream jsonInputStream, Configuration configuration) throws IOException {
        notNull(jsonInputStream, "json input stream can not be null");
        notNull(configuration, "configuration can not be null");

        try {
            return read(configuration.getProvider().parse(jsonInputStream), configuration);
        } finally {
            Utils.closeQuietly(jsonInputStream);
        }
    }

    public static JsonPath compile(String jsonPath, Filter... filters) {
        notEmpty(jsonPath, "json can not be null or empty");

        return new JsonPath(jsonPath, filters);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T read(Object json, String jsonPath, Filter... filters) {

        return new JsonReader().parse(json).read(jsonPath, filters);
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
