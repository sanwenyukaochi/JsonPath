package com.jayway.jsonpath.internal.spi.mapper;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.mapper.MappingException;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GsonMappingProvider implements MappingProvider {

    private static final Logger logger = LoggerFactory.getLogger(GsonMappingProvider.class);

    private final Factory<Gson> factory;

    public GsonMappingProvider(final Gson gson) {
        this(
                new Factory<Gson>() {
                    @Override
                    public Gson createInstance() {
                        return gson;
                    }
                });
    }

    public GsonMappingProvider(Factory<Gson> factory) {
        this.factory = factory;
    }

    public GsonMappingProvider() {
        super();
        try {
            Class.forName("com.google.gson.Gson");
            this.factory =
                    new Factory<Gson>() {
                        @Override
                        public Gson createInstance() {
                            return new Gson();
                        }
                    };
        } catch (ClassNotFoundException e) {
            logger.error("Gson not found on class path. No converters configured.");
            throw new JsonPathException("Gson not found on path", e);
        }
    }

    @Override
    public <T> T map(Object source, Class<T> targetType, Configuration configuration) {
        try {
            return factory.createInstance()
                    .getAdapter(targetType)
                    .fromJsonTree((JsonElement) source);
        } catch (Exception e) {
            throw new MappingException(e);
        }
    }

    @Override
    public <T> T map(Object source, TypeRef<T> targetType, Configuration configuration) {
        try {
            return (T)
                    factory.createInstance()
                            .getAdapter(TypeToken.get(targetType.getType()))
                            .fromJsonTree((JsonElement) source);
        } catch (Exception e) {
            throw new MappingException(e);
        }
    }
}
