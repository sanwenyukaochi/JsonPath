package com.jayway.jsonpath;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

import com.jayway.jsonpath.internal.ConvertUtils;
import com.jayway.jsonpath.internal.IOUtils;
import com.jayway.jsonpath.internal.PathToken;
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
    private JsonProvider jsonProvider;

    private JsonModel(String jsonObject, JsonProvider jsonProvider) {
        notNull(jsonObject, "json can not be null");

        this.jsonProvider = jsonProvider;
        this.jsonObject = jsonProvider.parse(jsonObject);
    }

    private JsonModel(Object jsonObject, JsonProvider jsonProvider) {
        notNull(jsonObject, "json can not be null");

        if (!jsonProvider.isContainer(jsonObject)) {
            throw new IllegalArgumentException("Invalid container object");
        }
        this.jsonProvider = jsonProvider;
        this.jsonObject = jsonObject;
    }

    private JsonModel(InputStream jsonInputStream, JsonProvider jsonProvider) {
        notNull(jsonInputStream, "jsonInputStream can not be null");
        this.jsonProvider = jsonProvider;
        this.jsonObject = jsonProvider.parse(jsonInputStream);
    }

    private JsonModel(URL jsonURL, JsonProvider jsonProvider) throws IOException {
        notNull(jsonURL, "jsonURL can not be null");

        InputStream jsonInputStream = null;
        try {
            jsonInputStream = jsonURL.openStream();
            this.jsonObject = jsonProvider.parse(jsonInputStream);
            this.jsonProvider = jsonProvider;
        } finally {
            IOUtils.closeQuietly(jsonInputStream);
        }
    }

    public boolean isList() {
        return jsonProvider.isList(jsonObject);
    }

    public boolean isMap() {
        return jsonProvider.isMap(jsonObject);
    }

    public void print() {
        System.out.println(toJson());
    }

    public boolean hasPath(String jsonPath) {
        return hasPath(JsonPath.compile(jsonPath));
    }

    public boolean hasPath(JsonPath jsonPath) {

        isTrue(jsonPath.isPathDefinite(), "hasPath can only be used for definite paths");

        try {
            get(jsonPath);
        } catch (InvalidPathException e) {
            return false;
        }
        return true;
    }

    public Object getJsonObject() {
        return this.jsonObject;
    }

    @SuppressWarnings({"unchecked"})
    public <T> T get(String jsonPath, Filter... filters) {
        return (T) get(JsonPath.compile(jsonPath, filters));
    }

    @SuppressWarnings({"unchecked"})
    public <T> T get(JsonPath jsonPath) {
        notNull(jsonPath, "jsonPath can not be null");

        return (T) jsonPath.read(jsonObject);
    }

    public ArrayOps opsForArray() {
        isTrue(jsonProvider.isList(jsonObject), "This JsonModel is not a JSON array");
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
        return jsonProvider.toJson(jsonObject);
    }

    public String toJson(String jsonPath, Filter... filters) {
        return toJson(JsonPath.compile(jsonPath, filters));
    }

    public String toJson(JsonPath jsonPath) {
        notNull(jsonPath, "jsonPath can not be null");

        return jsonProvider.toJson(get(jsonPath));
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

        if (!jsonProvider.isContainer(subModel)) {
            throw new InvalidModelException(
                    "The path "
                            + jsonPath.getPath()
                            + " returned an invalid model "
                            + (subModel != null ? subModel.getClass() : "null"));
        }

        return new JsonSubModel(subModel, this.jsonProvider, this, jsonPath);
    }

    public JsonModel getSubModelDetached(String jsonPath, Filter... filters) {
        return getSubModelDetached(JsonPath.compile(jsonPath, filters));
    }

    public JsonModel getSubModelDetached(JsonPath jsonPath) {
        notNull(jsonPath, "jsonPath can not be null");

        Object subModel = jsonPath.read(jsonObject);

        if (!jsonProvider.isContainer(subModel)) {
            throw new InvalidModelException(
                    "The path "
                            + jsonPath.getPath()
                            + " returned an invalid model "
                            + (subModel != null ? subModel.getClass() : "null"));
        }

        subModel = jsonProvider.clone(subModel);

        return new JsonModel(subModel, this.jsonProvider);
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

    public static JsonModel model(String json) {
        notEmpty(json, "json can not be null or empty");

        return new JsonModel(json, JsonProviderFactory.createProvider());
    }

    public static JsonModel create(String json) {
        return model(json);
    }

    public static JsonModel model(Object jsonObject) {
        notNull(jsonObject, "jsonObject can not be null");

        return new JsonModel(jsonObject, JsonProviderFactory.createProvider());
    }

    public static JsonModel create(Object jsonObject) {
        return model(jsonObject);
    }

    public static JsonModel model(URL url) throws IOException {
        notNull(url, "url can not be null");

        return new JsonModel(url, JsonProviderFactory.createProvider());
    }

    public static JsonModel create(URL url) throws IOException {
        return model(url);
    }

    public static JsonModel model(InputStream jsonInputStream) throws IOException {
        notNull(jsonInputStream, "jsonInputStream can not be null");

        return new JsonModel(jsonInputStream, JsonProviderFactory.createProvider());
    }

    public static JsonModel create(InputStream jsonInputStream) throws IOException {
        return model(jsonInputStream);
    }

    private <T> T getTargetObject(JsonPath jsonPath, Class<T> clazz) {
        notNull(jsonPath, "jsonPath can not be null");

        if (!jsonPath.isPathDefinite()) {
            throw new IndefinitePathException(jsonPath.getPath());
        }

        JsonProvider jsonProvider = JsonProviderFactory.createProvider();

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
                modelRef = currentToken.apply(modelRef, jsonProvider);
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
            return ConvertUtils.toInt(get(key));
        }

        @Override
        public Long getLong(String key) {
            return ConvertUtils.toLong(get(key));
        }

        @Override
        public Double getDouble(String key) {
            return ConvertUtils.toDouble(get(key));
        }

        @Override
        public String getString(String key) {
            return ConvertUtils.toString(get(key));
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
            Object transformed = transformer.transform(targetObject);
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
            Object transformed = transformer.transform(getTargetObject(jsonPath, List.class));
            setTargetObject(jsonPath, transformed);
            return this;
        }

        @Override
        public ArrayOps each(Transformer<Object> transformer) {

            List targetObject = getTargetObject(jsonPath, List.class);
            for (int i = 0; i < targetObject.size(); i++) {
                targetObject.set(i, transformer.transform(targetObject.get(i)));
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
                JsonProvider jsonProvider,
                JsonModel parent,
                JsonPath subModelPath) {
            super(jsonObject, jsonProvider);
            this.parent = parent;
            this.subModelPath = subModelPath;
        }
    }
}
