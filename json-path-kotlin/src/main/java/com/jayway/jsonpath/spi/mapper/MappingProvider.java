package com.jayway.jsonpath.spi.mapper;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.TypeRef;

public interface MappingProvider {

    <T> T map(Object source, Class<T> targetType, Configuration configuration);

    <T> T map(Object source, TypeRef<T> targetType, Configuration configuration);
}
