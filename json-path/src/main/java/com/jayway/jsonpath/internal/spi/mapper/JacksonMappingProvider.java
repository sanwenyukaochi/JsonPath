package com.jayway.jsonpath.internal.spi.mapper;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

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
        return objectMapper.convertValue(source, targetType);
    }
}
