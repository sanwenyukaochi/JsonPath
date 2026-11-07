package com.jayway.jsonpath.spi.mapper;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.TypeRef;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GsonMappingProvider implements MappingProvider {

    private static final Logger logger = LoggerFactory.getLogger(GsonMappingProvider.class);

    private final Callable<Gson> factory;

    public GsonMappingProvider(final Gson gson) {
        this(
                new Callable<Gson>() {
                    @Override
                    public Gson call() {
                        return gson;
                    }
                });
    }

    public GsonMappingProvider(Callable<Gson> factory) {
        this.factory = factory;
    }

    public GsonMappingProvider() {
        super();
        try {
            Class.forName("com.google.gson.Gson");
            this.factory =
                    new Callable<Gson>() {
                        @Override
                        public Gson call() {
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
            return factory.call().getAdapter(targetType).fromJsonTree((JsonElement) source);
        } catch (Exception e) {
            throw new MappingException(e);
        }
    }

    @Override
    public <T> T map(Object source, TypeRef<T> targetType, Configuration configuration) {
        try {
            return (T)
                    factory.call()
                            .getAdapter(TypeToken.get(targetType.getType()))
                            .fromJsonTree((JsonElement) source);
        } catch (Exception e) {
            throw new MappingException(e);
        }
    }
}
