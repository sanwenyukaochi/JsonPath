package com.jayway.jsonpath;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

import com.jayway.jsonpath.spi.JsonProviderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.type.CollectionType;

public class JsonModel {

    private static ObjectMapper objectMapper;

    private Object jsonObject;

    public JsonModel(String json) {
        this(JsonProviderFactory.getInstance().parse(json));
    }

    private JsonModel(Object jsonObject) {
        notNull(jsonObject, "json can not be null");

        if (!(jsonObject instanceof Map) && !(jsonObject instanceof List)) {
            throw new IllegalArgumentException("Invalid container object");
        }

        this.jsonObject = jsonObject;
    }

    private JsonModel(InputStream jsonInputStream) {
        notNull(jsonInputStream, "jsonInputStream can not be null");

        this.jsonObject = JsonProviderFactory.getInstance().parse(jsonInputStream);
    }

    private JsonModel(URL jsonURL) throws IOException {
        notNull(jsonURL, "jsonURL can not be null");

        InputStream jsonInputStream = null;
        try {
            jsonInputStream = jsonURL.openStream();
            this.jsonObject = JsonProviderFactory.getInstance().parse(jsonInputStream);
        } finally {
            IOUtils.closeQuietly(jsonInputStream);
        }
    }

    @SuppressWarnings({"unchecked"})
    public <T> T get(String jsonPath) {
        notEmpty(jsonPath, "jsonPath can not be null or empty");

        JsonPath path = JsonPath.compile(jsonPath);
        return (T) get(path);
    }

    @SuppressWarnings({"unchecked"})
    public <T> T get(JsonPath jsonPath) {
        notNull(jsonPath, "jsonPath can not be null");

        return (T) jsonPath.read(jsonObject);
    }

    public String toJson() {
        return JsonProviderFactory.getInstance().toJson(jsonObject);
    }

    public String toJson(String jsonPath) {
        notEmpty(jsonPath, "jsonPath can not be null or empty");

        return JsonProviderFactory.getInstance().toJson(get(jsonPath));
    }

    public String toJson(JsonPath jsonPath) {
        notNull(jsonPath, "jsonPath can not be null");

        return JsonProviderFactory.getInstance().toJson(get(jsonPath));
    }

    public JsonModel getSubModel(String jsonPath) {
        notEmpty(jsonPath, "jsonPath can not be null or empty");

        JsonPath path = JsonPath.compile(jsonPath);
        return getSubModel(path);
    }

    public JsonModel getSubModel(JsonPath jsonPath) {
        notNull(jsonPath, "jsonPath can not be null");

        Object subModel = jsonPath.read(jsonObject);

        if (!(subModel instanceof Map) && !(subModel instanceof List)) {
            throw new InvalidModelPathException(
                    "The path "
                            + jsonPath.getPath()
                            + " returned an invalid model "
                            + (subModel != null ? subModel.getClass() : "null"));
        }

        return new JsonModel(subModel);
    }

    public MappingModelReader map(final String jsonPath) {
        notEmpty(jsonPath, "jsonPath can not be null or empty");

        return new DefaultMappingModelReader(JsonModel.this.get(jsonPath));
    }

    public MappingModelReader map(final JsonPath jsonPath) {
        notNull(jsonPath, "jsonPath can not be null");

        return new DefaultMappingModelReader(JsonModel.this.get(jsonPath));
    }

    public static JsonModel create(String json) {
        notEmpty(json, "json can not be null or empty");

        return new JsonModel(json);
    }

    public static JsonModel create(Object jsonObject) {
        notNull(jsonObject, "jsonObject can not be null");

        return new JsonModel(jsonObject);
    }

    public static JsonModel create(URL url) throws IOException {
        notNull(url, "url can not be null");

        return new JsonModel(url);
    }

    public static JsonModel create(InputStream jsonInputStream) throws IOException {
        notNull(jsonInputStream, "jsonInputStream can not be null");

        return new JsonModel(jsonInputStream);
    }

    public interface MappingModelReader {

        ListMappingModelReader toList();

        <T> List<T> toListOf(Class<T> targetClass);

        <T> Set<T> toSetOf(Class<T> targetClass);

        <T> T to(Class<T> targetClass);
    }

    public interface ListMappingModelReader {
        <T> List<T> of(Class<T> targetClass);
    }

    private static class DefaultMappingModelReader
            implements MappingModelReader, ListMappingModelReader {
        private ObjectMapper objectMapper;
        private Object model;

        private DefaultMappingModelReader(Object model) {
            this.model = model;
            this.objectMapper = JsonModel.getObjectMapper();
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
            CollectionType colType =
                    objectMapper.getTypeFactory().constructCollectionType(List.class, targetClass);
            return objectMapper.convertValue(model, colType);
        }

        @Override
        public <T> Set<T> toSetOf(Class<T> targetClass) {
            if (!(model instanceof List)) {
                Set setModel = new HashSet();
                setModel.add(model);
                model = setModel;
            }
            CollectionType colType =
                    objectMapper.getTypeFactory().constructCollectionType(Set.class, targetClass);
            return objectMapper.convertValue(model, colType);
        }

        @Override
        public <T> T to(Class<T> targetClass) {
            return objectMapper.convertValue(model, targetClass);
        }
    }

    private static ObjectMapper getObjectMapper() {
        if (JsonModel.objectMapper == null) {
            synchronized (JsonModel.class) {
                JsonModel.objectMapper = new ObjectMapper();
            }
        }
        return JsonModel.objectMapper;
    }
}
