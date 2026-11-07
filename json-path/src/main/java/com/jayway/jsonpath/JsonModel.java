package com.jayway.jsonpath;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

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
import org.apache.commons.io.IOUtils;

public class JsonModel {

    private Object jsonObject;
    private JsonProvider jsonProvider;

    private JsonModel(String jsonObject, JsonProvider jsonProvider) {
        notNull(jsonObject, "json can not be null");

        this.jsonProvider = jsonProvider;
        this.jsonObject = jsonProvider.parse(jsonObject);
    }

    private JsonModel(Object jsonObject, JsonProvider jsonProvider) {
        notNull(jsonObject, "json can not be null");

        if (!(jsonObject instanceof Map) && !(jsonObject instanceof List)) {
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

    public ArrayOps opsForArray(String jsonPath) {
        return opsForArray(JsonPath.compile(jsonPath));
    }

    public ArrayOps opsForArray(JsonPath jsonPath) {
        notNull(jsonPath, "jsonPath can not be null");

        List<Object> opsTarget = getTargetObject(jsonPath, List.class);

        return new DefaultArrayOps(opsTarget);
    }

    public ObjectOps opsForObject(String jsonPath) {
        return opsForObject(JsonPath.compile(jsonPath));
    }

    public ObjectOps opsForObject(JsonPath jsonPath) {
        notNull(jsonPath, "jsonPath can not be null");

        Map<String, Object> opsTarget = getTargetObject(jsonPath, Map.class);

        return new DefaultObjectOps(opsTarget);
    }

    public String getJson() {
        return jsonProvider.toJson(jsonObject);
    }

    public String getJson(String jsonPath, Filter... filters) {
        return getJson(JsonPath.compile(jsonPath, filters));
    }

    public String getJson(JsonPath jsonPath) {
        notNull(jsonPath, "jsonPath can not be null");

        return jsonProvider.toJson(get(jsonPath));
    }

    public JsonModel getModel(String jsonPath, Filter... filters) {
        return getModel(JsonPath.compile(jsonPath, filters));
    }

    public JsonModel getModel(JsonPath jsonPath) {
        notNull(jsonPath, "jsonPath can not be null");

        Object subModel = jsonPath.read(jsonObject);

        if (!(subModel instanceof Map) && !(subModel instanceof List)) {
            throw new InvalidModelPathException(
                    "The path "
                            + jsonPath.getPath()
                            + " returned an invalid model "
                            + (subModel != null ? subModel.getClass() : "null"));
        }

        return new JsonModel(subModel, this.jsonProvider);
    }

    public MappingModelReader map(String jsonPath) {
        return map(JsonPath.compile(jsonPath));
    }

    public MappingModelReader map(JsonPath jsonPath) {
        notNull(jsonPath, "jsonPath can not be null");

        return new DefaultMappingModelReader(JsonModel.this.get(jsonPath));
    }

    public static JsonModel create(String json) {
        notEmpty(json, "json can not be null or empty");

        return new JsonModel(json, JsonProviderFactory.createProvider());
    }

    public static JsonModel create(Object jsonObject) {
        notNull(jsonObject, "jsonObject can not be null");

        return new JsonModel(jsonObject, JsonProviderFactory.createProvider());
    }

    public static JsonModel create(URL url) throws IOException {
        notNull(url, "url can not be null");

        return new JsonModel(url, JsonProviderFactory.createProvider());
    }

    public static JsonModel create(InputStream jsonInputStream) throws IOException {
        notNull(jsonInputStream, "jsonInputStream can not be null");

        return new JsonModel(jsonInputStream, JsonProviderFactory.createProvider());
    }

    private <T> T getTargetObject(JsonPath jsonPath, Class<T> clazz) {
        notNull(jsonPath, "jsonPath can not be null");

        if (!jsonPath.isPathDefinite()) {
            throw new IndefinitePathException(jsonPath.getPath());
        }

        JsonProvider jsonProvider = JsonProviderFactory.createProvider();

        Object modelRef = jsonObject;

        LinkedList<PathToken> tokens = jsonPath.getTokenizer().getPathTokens();

        PathToken currentToken;
        do {
            currentToken = tokens.poll();
            modelRef = currentToken.apply(modelRef, jsonProvider);
        } while (!tokens.isEmpty());

        if (modelRef.getClass().isAssignableFrom(clazz)) {
            throw new InvalidModelPathException(
                    jsonPath + " does nor refer to a Map but " + currentToken.getClass().getName());
        }
        return clazz.cast(modelRef);
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

        ObjectOps putAll(Map<String, Object> map);

        ObjectOps remove(String key);

        <T> T to(Class<T> targetClass);
    }

    public interface ArrayOps {
        List<Object> getTarget();

        ArrayOps add(Object o);

        ArrayOps addAll(Collection<Object> collection);

        ArrayOps remove(Object o);

        ListMappingModelReader toList();

        <T> List<T> toListOf(Class<T> targetClass);

        <T> Set<T> toSetOf(Class<T> targetClass);
    }

    private static class DefaultObjectOps implements ObjectOps {

        private Map<String, Object> opsTarget;

        private DefaultObjectOps(Map<String, Object> opsTarget) {

            this.opsTarget = opsTarget;
        }

        @Override
        public Map<String, Object> getTarget() {
            return opsTarget;
        }

        @Override
        public boolean containsKey(String key) {
            return opsTarget.containsKey(key);
        }

        @Override
        public ObjectOps put(String key, Object value) {
            opsTarget.put(key, value);
            return this;
        }

        @Override
        public ObjectOps putAll(Map<String, Object> map) {
            opsTarget.putAll(map);
            return this;
        }

        @Override
        public ObjectOps remove(String key) {
            opsTarget.remove(key);
            return this;
        }

        @Override
        public <T> T to(Class<T> targetClass) {
            return new DefaultMappingModelReader(opsTarget).to(targetClass);
        }
    }

    private static class DefaultArrayOps implements ArrayOps {

        private List<Object> opsTarget;

        private DefaultArrayOps(List<Object> opsTarget) {
            this.opsTarget = opsTarget;
        }

        @Override
        public List<Object> getTarget() {
            return opsTarget;
        }

        @Override
        public ArrayOps add(Object o) {
            opsTarget.add(o);
            return this;
        }

        @Override
        public ArrayOps addAll(Collection<Object> collection) {
            opsTarget.addAll(collection);
            return this;
        }

        @Override
        public ArrayOps remove(Object o) {
            opsTarget.remove(o);
            return this;
        }

        @Override
        public ListMappingModelReader toList() {
            return new DefaultMappingModelReader(opsTarget);
        }

        @Override
        public <T> List<T> toListOf(Class<T> targetClass) {
            return new DefaultMappingModelReader(opsTarget).toListOf(targetClass);
        }

        @Override
        public <T> Set<T> toSetOf(Class<T> targetClass) {
            return new DefaultMappingModelReader(opsTarget).toSetOf(targetClass);
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
            if (!(model instanceof List)) {
                model = asList(model);
            }
            return MappingProviderFactory.createProvider()
                    .convertValue(model, List.class, targetClass);
        }

        @Override
        public <T> Set<T> toSetOf(Class<T> targetClass) {
            if (!(model instanceof List)) {
                Set setModel = new HashSet();
                setModel.add(model);
                model = setModel;
            }
            return MappingProviderFactory.createProvider()
                    .convertValue(model, Set.class, targetClass);
        }

        @Override
        public <T> T to(Class<T> targetClass) {
            return MappingProviderFactory.createProvider().convertValue(model, targetClass);
        }
    }
}
