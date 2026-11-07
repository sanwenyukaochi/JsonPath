package com.jayway.jsonpath;

import static com.jayway.jsonpath.internal.Utils.isTrue;
import static com.jayway.jsonpath.internal.Utils.notNull;
import static java.util.Arrays.asList;

import com.jayway.jsonpath.internal.JsonFormatter;
import com.jayway.jsonpath.internal.JsonReader;
import com.jayway.jsonpath.internal.PathToken;
import com.jayway.jsonpath.internal.Utils;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;
import com.jayway.jsonpath.spi.MappingProviderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonModel {

    private static final JsonPath JSON_PATH_ROOT = JsonPath.compile("$");
    private Object jsonObject;
    private Configuration configuration;

    private JsonModel(Object jsonObject, Configuration configuration) {
        notNull(jsonObject, "json can not be null");
        notNull(configuration, "configuration can not be null");

        if (!configuration.getProvider().isContainer(jsonObject)) {
            throw new IllegalArgumentException("Invalid container object");
        }
        this.configuration = configuration;
        this.jsonObject = jsonObject;
    }

    public boolean isList() {
        return configuration.getProvider().isArray(jsonObject);
    }

    public boolean isMap() {
        return configuration.getProvider().isMap(jsonObject);
    }

    public void print() {
        String json = toJson();
        System.out.println(JsonFormatter.prettyPrint(json));
    }

    public boolean hasPath(String jsonPath) {
        return hasPath(JsonPath.compile(jsonPath));
    }

    public boolean hasPath(JsonPath jsonPath) {

        isTrue(jsonPath.isPathDefinite(), "hasPath can only be used for definite paths");

        try {
            jsonPath.read(jsonObject, configuration.options(Option.THROW_ON_MISSING_PROPERTY));
        } catch (PathNotFoundException e) {
            return false;
        }
        return true;
    }

    public Object getJsonObject() {
        return this.jsonObject;
    }

    @SuppressWarnings({"unchecked"})
    public <T> T get(String jsonPath, Filter... filters) {
        return get(JsonPath.compile(jsonPath, filters));
    }

    @SuppressWarnings({"unchecked"})
    public <T> T get(JsonPath jsonPath) {
        notNull(jsonPath, "jsonPath can not be null");

        return jsonPath.read(jsonObject, configuration);
    }

    public ArrayOps opsForArray() {
        isTrue(
                configuration.getProvider().isArray(jsonObject),
                "This JsonModel is not a JSON array");
        return opsForArray(JSON_PATH_ROOT);
    }

    public ArrayOps opsForArray(String jsonPath) {
        return opsForArray(JsonPath.compile(jsonPath));
    }

    public ArrayOps opsForArray(JsonPath jsonPath) {
        notNull(jsonPath, "jsonPath can not be null");
        return new DefaultArrayOps(jsonPath);
    }

    public ObjectOps opsForObject() {
        return opsForObject(JSON_PATH_ROOT);
    }

    public ObjectOps opsForObject(String jsonPath) {
        return opsForObject(JsonPath.compile(jsonPath));
    }

    public ObjectOps opsForObject(JsonPath jsonPath) {
        notNull(jsonPath, "jsonPath can not be null");
        return new DefaultObjectOps(jsonPath);
    }

    public String toJson() {
        return toJson(false);
    }

    public String toJson(boolean prettyPrint) {
        String json = configuration.getProvider().toJson(jsonObject);
        if (prettyPrint) return JsonFormatter.prettyPrint(json);
        else return json;
    }

    public String toJson(String jsonPath, Filter... filters) {
        return toJson(JsonPath.compile(jsonPath, filters));
    }

    public String toJson(JsonPath jsonPath) {
        notNull(jsonPath, "jsonPath can not be null");

        return configuration.getProvider().toJson(get(jsonPath));
    }

    public JsonModel getSubModel(String jsonPath) {
        return getSubModel(JsonPath.compile(jsonPath));
    }

    public JsonModel getSubModel(JsonPath jsonPath) {
        notNull(jsonPath, "jsonPath can not be null");

        isTrue(
                jsonPath.isPathDefinite(),
                "You can only get subModels with a definite path. Use getDetachedModel if path is not definite.");

        Object subModel = jsonPath.read(jsonObject);

        if (!configuration.getProvider().isContainer(subModel)) {
            throw new InvalidModelException(
                    "The path "
                            + jsonPath.getPath()
                            + " returned an invalid model "
                            + (subModel != null ? subModel.getClass() : "null"));
        }

        return new JsonSubModel(subModel, this.configuration, this, jsonPath);
    }

    public JsonModel getSubModelDetached(String jsonPath, Filter... filters) {
        return getSubModelDetached(JsonPath.compile(jsonPath, filters));
    }

    public JsonModel getSubModelDetached(JsonPath jsonPath) {
        notNull(jsonPath, "jsonPath can not be null");

        Object subModel = jsonPath.read(jsonObject, configuration);

        if (!configuration.getProvider().isContainer(subModel)) {
            throw new InvalidModelException(
                    "The path "
                            + jsonPath.getPath()
                            + " returned an invalid model "
                            + (subModel != null ? subModel.getClass() : "null"));
        }

        subModel = configuration.getProvider().clone(subModel);

        return new JsonModel(subModel, configuration);
    }

    public MappingModelReader map() {
        return new DefaultMappingModelReader(this.jsonObject);
    }

    public MappingModelReader map(String jsonPath, Filter... filters) {
        return map(JsonPath.compile(jsonPath, filters));
    }

    public MappingModelReader map(JsonPath jsonPath) {
        notNull(jsonPath, "jsonPath can not be null");

        return new DefaultMappingModelReader(JsonModel.this.get(jsonPath));
    }

    public static JsonModel model(JsonProvider jsonProvider, String json) {
        notNull(jsonProvider, "jsonProvider can not be null");
        notNull(json, "jsonObject can not be null");
        return new JsonModel(
                new JsonReader(jsonProvider).parse(json).json(),
                Configuration.builder().jsonProvider(jsonProvider).build());
    }

    public static JsonModel model(String json) {
        return model(JsonProviderFactory.createProvider(), json);
    }

    public static JsonModel create(String json) {
        return model(json);
    }

    public static JsonModel model(JsonProvider jsonProvider, Object jsonObject) {
        notNull(jsonProvider, "jsonProvider can not be null");
        notNull(jsonObject, "jsonObject can not be null");
        return new JsonModel(
                jsonObject, Configuration.builder().jsonProvider(jsonProvider).build());
    }

    public static JsonModel model(Object jsonObject) {
        return model(JsonProviderFactory.createProvider(), jsonObject);
    }

    public static JsonModel create(Object jsonObject) {
        return model(jsonObject);
    }

    public static JsonModel model(JsonProvider jsonProvider, URL url) throws IOException {
        notNull(jsonProvider, "jsonProvider can not be null");
        notNull(url, "url can not be null");

        return new JsonModel(
                new JsonReader(jsonProvider).parse(url).json(),
                Configuration.builder().jsonProvider(jsonProvider).build());
    }

    public static JsonModel model(URL url) throws IOException {
        return model(JsonProviderFactory.createProvider(), url);
    }

    public static JsonModel create(URL url) throws IOException {
        return model(url);
    }

    public static JsonModel model(JsonProvider jsonProvider, InputStream jsonInputStream)
            throws IOException {
        notNull(jsonProvider, "jsonProvider can not be null");
        notNull(jsonInputStream, "jsonInputStream can not be null");

        return new JsonModel(
                new JsonReader(jsonProvider).parse(jsonInputStream).json(),
                Configuration.builder().jsonProvider(jsonProvider).build());
    }

    public static JsonModel model(InputStream jsonInputStream) throws IOException {
        return model(JsonProviderFactory.createProvider(), jsonInputStream);
    }

    public static JsonModel create(InputStream jsonInputStream) throws IOException {
        return model(jsonInputStream);
    }

    private <T> T getTargetObject(JsonPath jsonPath, Class<T> clazz) {
        notNull(jsonPath, "jsonPath can not be null");

        if (!jsonPath.isPathDefinite()) {
            throw new IndefinitePathException(jsonPath.getPath());
        }

        Object modelRef = jsonObject;

        if (jsonPath.getTokenizer().size() == 1) {
            PathToken onlyToken = jsonPath.getTokenizer().iterator().next();
            if ("$".equals(onlyToken.getFragment())) {
                return clazz.cast(modelRef);
            }
        } else {

            LinkedList<PathToken> tokens = jsonPath.getTokenizer().getPathTokens();

            PathToken currentToken;
            do {
                currentToken = tokens.poll();
                modelRef = currentToken.apply(modelRef, this.configuration);
            } while (!tokens.isEmpty());

            if (modelRef.getClass().isAssignableFrom(clazz)) {
                throw new InvalidModelException(
                        jsonPath
                                + " does nor refer to a Map but "
                                + currentToken.getClass().getName());
            }
            return clazz.cast(modelRef);
        }
        throw new InvalidModelException();
    }

    private void setTargetObject(JsonPath jsonPath, Object newValue) {
        JsonPath setterPath = jsonPath.copy();
        PathToken pathToken = setterPath.getTokenizer().removeLastPathToken();

        if (pathToken.isRootToken()) {
            if (this instanceof JsonSubModel) {
                JsonSubModel thisModel = (JsonSubModel) this;

                thisModel.parent.setTargetObject(thisModel.subModelPath, newValue);
            } else {
                this.jsonObject = newValue;
            }
        } else {
            if (pathToken.isArrayIndexToken()) {
                int arrayIndex = pathToken.getArrayIndex();
                opsForArray(setterPath).set(arrayIndex, newValue);
            } else {
                opsForObject(setterPath).put(pathToken.getFragment(), newValue);
            }
        }
    }

    public interface ObjectMappingModelReader {

        <T> T to(Class<T> targetClass);
    }

    public interface ListMappingModelReader {

        <T> List<T> of(Class<T> targetClass);

        ListMappingModelReader toList();

        <T> List<T> toListOf(Class<T> targetClass);

        <T> Set<T> toSetOf(Class<T> targetClass);
    }

    public interface MappingModelReader extends ListMappingModelReader, ObjectMappingModelReader {}

    public interface ObjectOps {

        Map<String, Object> getTarget();

        boolean containsKey(String key);

        ObjectOps put(String key, Object value);

        ObjectOps putIfAbsent(String key, Object value);

        Object get(String key);

        Integer getInteger(String key);

        Long getLong(String key);

        Double getDouble(String key);

        String getString(String key);

        ObjectOps putAll(Map<String, Object> map);

        ObjectOps remove(String key);

        ObjectOps transform(Transformer<Map<String, Object>> transformer);

        <T> T to(Class<T> targetClass);
    }

    public interface ArrayOps {

        List<Object> getTarget();

        ArrayOps add(Object o);

        ArrayOps addAll(Collection<Object> collection);

        ArrayOps remove(Object o);

        int size();

        ArrayOps set(int index, Object value);

        ArrayOps transform(Transformer<List<Object>> transformer);

        ArrayOps each(Transformer<Object> transformer);

        ListMappingModelReader toList();

        <T> List<T> toListOf(Class<T> targetClass);

        <T> Set<T> toSetOf(Class<T> targetClass);
    }

    private class DefaultObjectOps implements ObjectOps {

        private JsonPath jsonPath;

        private DefaultObjectOps(JsonPath jsonPath) {
            this.jsonPath = jsonPath;
        }

        @Override
        public Map<String, Object> getTarget() {
            return getTargetObject(jsonPath, Map.class);
        }

        @Override
        public boolean containsKey(String key) {
            return getTargetObject(jsonPath, Map.class).containsKey(key);
        }

        @Override
        public ObjectOps put(String key, Object value) {
            getTargetObject(jsonPath, Map.class).put(key, value);
            return this;
        }

        @Override
        public ObjectOps putIfAbsent(String key, Object value) {
            Map targetObject = getTargetObject(jsonPath, Map.class);
            if (!targetObject.containsKey(key)) {
                targetObject.put(key, value);
            }
            return this;
        }

        @Override
        public Object get(String key) {
            return getTargetObject(jsonPath, Map.class).get(key);
        }

        @Override
        public Integer getInteger(String key) {
            return Utils.toInt(get(key));
        }

        @Override
        public Long getLong(String key) {
            return Utils.toLong(get(key));
        }

        @Override
        public Double getDouble(String key) {
            return Utils.toDouble(get(key));
        }

        @Override
        public String getString(String key) {
            return Utils.toString(get(key));
        }

        @Override
        public ObjectOps putAll(Map<String, Object> map) {
            getTargetObject(jsonPath, Map.class).putAll(map);
            return this;
        }

        @Override
        public ObjectOps remove(String key) {
            getTargetObject(jsonPath, Map.class).remove(key);
            return this;
        }

        @Override
        public ObjectOps transform(Transformer<Map<String, Object>> transformer) {
            Map targetObject = getTargetObject(jsonPath, Map.class);
            Object transformed = transformer.transform(targetObject, configuration);
            setTargetObject(jsonPath, transformed);
            return this;
        }

        @Override
        public <T> T to(Class<T> targetClass) {
            Map targetObject = getTargetObject(jsonPath, Map.class);
            return new DefaultMappingModelReader(targetObject).to(targetClass);
        }
    }

    private class DefaultArrayOps implements ArrayOps {

        private JsonPath jsonPath;

        private DefaultArrayOps(JsonPath jsonPath) {
            this.jsonPath = jsonPath;
        }

        @Override
        public List<Object> getTarget() {
            return getTargetObject(jsonPath, List.class);
        }

        @Override
        public ArrayOps add(Object o) {
            getTargetObject(jsonPath, List.class).add(o);
            return this;
        }

        @Override
        public ArrayOps addAll(Collection<Object> collection) {
            getTargetObject(jsonPath, List.class).addAll(collection);
            return this;
        }

        @Override
        public ArrayOps remove(Object o) {
            getTargetObject(jsonPath, List.class).remove(o);
            return this;
        }

        @Override
        public int size() {
            return getTargetObject(jsonPath, List.class).size();
        }

        @Override
        public ArrayOps set(int index, Object value) {
            getTargetObject(jsonPath, List.class).set(index, value);
            return this;
        }

        @Override
        public ListMappingModelReader toList() {
            return new DefaultMappingModelReader(getTargetObject(jsonPath, List.class));
        }

        @Override
        public ArrayOps transform(Transformer<List<Object>> transformer) {
            Object transformed =
                    transformer.transform(getTargetObject(jsonPath, List.class), configuration);
            setTargetObject(jsonPath, transformed);
            return this;
        }

        @Override
        public ArrayOps each(Transformer<Object> transformer) {

            List targetObject = getTargetObject(jsonPath, List.class);
            for (int i = 0; i < targetObject.size(); i++) {
                targetObject.set(i, transformer.transform(targetObject.get(i), configuration));
            }
            return this;
        }

        @Override
        public <T> List<T> toListOf(Class<T> targetClass) {
            return new DefaultMappingModelReader(getTargetObject(jsonPath, List.class))
                    .toListOf(targetClass);
        }

        @Override
        public <T> Set<T> toSetOf(Class<T> targetClass) {
            return new DefaultMappingModelReader(getTargetObject(jsonPath, List.class))
                    .toSetOf(targetClass);
        }
    }

    private static class DefaultMappingModelReader implements MappingModelReader {
        private Object model;

        private DefaultMappingModelReader(Object model) {
            this.model = model;
        }

        @Override
        public ListMappingModelReader toList() {
            return this;
        }

        @Override
        public <T> List<T> of(Class<T> targetClass) {
            return toListOf(targetClass);
        }

        @Override
        public <T> List<T> toListOf(Class<T> targetClass) {
            Object modelRef = model;
            if (!(modelRef instanceof List)) {
                modelRef = asList(modelRef);
            }
            return MappingProviderFactory.createProvider()
                    .convertValue(modelRef, List.class, targetClass);
        }

        @Override
        public <T> Set<T> toSetOf(Class<T> targetClass) {
            Object modelRef = model;
            if (!(modelRef instanceof List)) {
                Set setModel = new HashSet();
                setModel.add(model);
                modelRef = setModel;
            }
            return MappingProviderFactory.createProvider()
                    .convertValue(modelRef, Set.class, targetClass);
        }

        @Override
        public <T> T to(Class<T> targetClass) {
            return MappingProviderFactory.createProvider().convertValue(model, targetClass);
        }
    }

    private static class JsonSubModel extends JsonModel {

        private final JsonModel parent;
        private final JsonPath subModelPath;

        private JsonSubModel(
                Object jsonObject,
                Configuration configuration,
                JsonModel parent,
                JsonPath subModelPath) {
            super(jsonObject, configuration);
            this.parent = parent;
            this.subModelPath = subModelPath;
        }
    }
}
