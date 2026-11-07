package com.jayway.jsonpath.internal.spi.mapper;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.mapper.MappingException;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;

public class JacksonMappingProvider implements MappingProvider {

    private final ObjectMapper objectMapper;

    public JacksonMappingProvider() {
        this(new ObjectMapper());
    }

    public JacksonMappingProvider(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> T map(Object source, Class<T> targetType, Configuration configuration) {
        if (source == null) {
            return null;
        }
        try {
            return objectMapper.convertValue(source, targetType);
        } catch (Exception e) {
            throw new MappingException(e);
        }
    }

    @Override
    public <T> T map(Object source, final TypeRef<T> targetType, Configuration configuration) {
        if (source == null) {
            return null;
        }
        JavaType type = objectMapper.getTypeFactory().constructType(targetType.getType());

        try {
            return (T) objectMapper.convertValue(source, type);
        } catch (Exception e) {
            throw new MappingException(e);
        }
    }
}
