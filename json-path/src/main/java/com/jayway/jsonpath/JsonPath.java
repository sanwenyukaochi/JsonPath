package com.jayway.jsonpath;

import static com.jayway.jsonpath.internal.Utils.isTrue;
import static com.jayway.jsonpath.internal.Utils.notEmpty;
import static com.jayway.jsonpath.internal.Utils.notNull;

import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.JsonReader;
import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.PathCompiler;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.Utils;
import com.jayway.jsonpath.spi.json.JsonProvider;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class JsonPath {

    private final Path path;

    private JsonPath(String jsonPath, Predicate[] filters) {
        notNull(jsonPath, "path can not be null");
        this.path = PathCompiler.compile(jsonPath, filters);
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

    @SuppressWarnings("unchecked")
    public <T> T read(Object jsonObject, Configuration configuration) {
        boolean optAsPathList = configuration.containsOption(Option.AS_PATH_LIST);
        boolean optAlwaysReturnList = configuration.containsOption(Option.ALWAYS_RETURN_LIST);
        boolean optSuppressExceptions = configuration.containsOption(Option.SUPPRESS_EXCEPTIONS);

        try {
            if (optAsPathList) {
                return (T) path.evaluate(jsonObject, jsonObject, configuration).getPath();
            } else {
                Object res = path.evaluate(jsonObject, jsonObject, configuration).getValue(false);
                if (optAlwaysReturnList && path.isDefinite()) {
                    Object array = configuration.jsonProvider().createArray();
                    configuration.jsonProvider().setArrayIndex(array, 0, res);
                    return (T) array;
                } else {
                    return (T) res;
                }
            }
        } catch (RuntimeException e) {
            if (!optSuppressExceptions) {
                throw e;
            }
        }
        if (optAsPathList) {
            return (T) configuration.jsonProvider().createArray();
        } else {
            if (optAlwaysReturnList) {
                return (T) configuration.jsonProvider().createArray();
            } else {
                return (T) (path.isDefinite() ? null : configuration.jsonProvider().createArray());
            }
        }
    }

    public <T> T set(Object jsonObject, Object newVal, Configuration configuration) {
        notNull(jsonObject, "json can not be null");
        notNull(configuration, "configuration can not be null");
        EvaluationContext evaluationContext =
                path.evaluate(jsonObject, jsonObject, configuration, true);
        for (PathRef updateOperation : evaluationContext.updateOperations()) {
            updateOperation.set(newVal, configuration);
        }
        return resultByConfiguration(jsonObject, configuration, evaluationContext);
    }

    public <T> T delete(Object jsonObject, Configuration configuration) {
        notNull(jsonObject, "json can not be null");
        notNull(configuration, "configuration can not be null");
        EvaluationContext evaluationContext =
                path.evaluate(jsonObject, jsonObject, configuration, true);
        for (PathRef updateOperation : evaluationContext.updateOperations()) {
            updateOperation.delete(configuration);
        }
        return resultByConfiguration(jsonObject, configuration, evaluationContext);
    }

    public <T> T add(Object jsonObject, Object value, Configuration configuration) {
        notNull(jsonObject, "json can not be null");
        notNull(configuration, "configuration can not be null");
        EvaluationContext evaluationContext =
                path.evaluate(jsonObject, jsonObject, configuration, true);
        for (PathRef updateOperation : evaluationContext.updateOperations()) {
            updateOperation.add(value, configuration);
        }
        return resultByConfiguration(jsonObject, configuration, evaluationContext);
    }

    public <T> T put(Object jsonObject, String key, Object value, Configuration configuration) {
        notNull(jsonObject, "json can not be null");
        notEmpty(key, "key can not be null or empty");
        notNull(configuration, "configuration can not be null");
        EvaluationContext evaluationContext =
                path.evaluate(jsonObject, jsonObject, configuration, true);
        for (PathRef updateOperation : evaluationContext.updateOperations()) {
            updateOperation.put(key, value, configuration);
        }
        return resultByConfiguration(jsonObject, configuration, evaluationContext);
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(String json) {
        return read(json, Configuration.defaultConfiguration());
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(String json, Configuration configuration) {
        notEmpty(json, "json can not be null or empty");
        notNull(configuration, "jsonProvider can not be null");

        return read(configuration.jsonProvider().parse(json), configuration);
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(URL jsonURL) throws IOException {
        return read(jsonURL, Configuration.defaultConfiguration());
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
            return read(fis, configuration);
        } finally {
            Utils.closeQuietly(fis);
        }
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(InputStream jsonInputStream) throws IOException {
        return read(jsonInputStream, Configuration.defaultConfiguration());
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(InputStream jsonInputStream, Configuration configuration) throws IOException {
        notNull(jsonInputStream, "json input stream can not be null");
        notNull(configuration, "configuration can not be null");

        return read(jsonInputStream, "UTF-8", configuration);
    }

    @SuppressWarnings({"unchecked"})
    public <T> T read(InputStream jsonInputStream, String charset, Configuration configuration)
            throws IOException {
        notNull(jsonInputStream, "json input stream can not be null");
        notNull(charset, "charset can not be null");
        notNull(configuration, "configuration can not be null");

        try {
            return read(
                    configuration.jsonProvider().parse(jsonInputStream, charset), configuration);
        } finally {
            Utils.closeQuietly(jsonInputStream);
        }
    }

    public static JsonPath compile(String jsonPath, Predicate... filters) {
        notEmpty(jsonPath, "json can not be null or empty");

        return new JsonPath(jsonPath, filters);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T read(Object json, String jsonPath, Predicate... filters) {
        return parse(json).read(jsonPath, filters);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T read(String json, String jsonPath, Predicate... filters) {
        return new JsonReader().parse(json).read(jsonPath, filters);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T read(URL jsonURL, String jsonPath, Predicate... filters)
            throws IOException {
        return new JsonReader().parse(jsonURL).read(jsonPath, filters);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T read(File jsonFile, String jsonPath, Predicate... filters)
            throws IOException {
        return new JsonReader().parse(jsonFile).read(jsonPath, filters);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T read(InputStream jsonInputStream, String jsonPath, Predicate... filters)
            throws IOException {
        return new JsonReader().parse(jsonInputStream).read(jsonPath, filters);
    }

    public static ParseContext using(Configuration configuration) {
        return new JsonReader(configuration);
    }

    @Deprecated
    public static ParseContext using(JsonProvider provider) {
        return new JsonReader(Configuration.builder().jsonProvider(provider).build());
    }

    public static DocumentContext parse(Object json) {
        return new JsonReader().parse(json);
    }

    public static DocumentContext parse(String json) {
        return new JsonReader().parse(json);
    }

    public static DocumentContext parse(InputStream json) {
        return new JsonReader().parse(json);
    }

    public static DocumentContext parse(File json) throws IOException {
        return new JsonReader().parse(json);
    }

    public static DocumentContext parse(URL json) throws IOException {
        return new JsonReader().parse(json);
    }

    public static DocumentContext parse(Object json, Configuration configuration) {
        return new JsonReader(configuration).parse(json);
    }

    public static DocumentContext parse(String json, Configuration configuration) {
        return new JsonReader(configuration).parse(json);
    }

    public static DocumentContext parse(InputStream json, Configuration configuration) {
        return new JsonReader(configuration).parse(json);
    }

    public static DocumentContext parse(File json, Configuration configuration) throws IOException {
        return new JsonReader(configuration).parse(json);
    }

    public static DocumentContext parse(URL json, Configuration configuration) throws IOException {
        return new JsonReader(configuration).parse(json);
    }

    private <T> T resultByConfiguration(
            Object jsonObject, Configuration configuration, EvaluationContext evaluationContext) {
        if (configuration.containsOption(Option.AS_PATH_LIST)) {
            return (T) evaluationContext.getPathList();
        } else {
            return (T) jsonObject;
        }
    }
}
