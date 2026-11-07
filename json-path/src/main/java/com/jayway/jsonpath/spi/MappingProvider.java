package com.jayway.jsonpath.spi;

import java.util.Collection;

public interface MappingProvider {

    public <T> T convertValue(Object fromValue, Class<T> toValueType)
            throws IllegalArgumentException;

    public <T extends Collection<E>, E> T convertValue(
            Object fromValue, Class<T> collectionType, Class<E> elementType)
            throws IllegalArgumentException;
}
