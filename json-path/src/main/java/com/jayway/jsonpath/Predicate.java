package com.jayway.jsonpath;

import com.jayway.jsonpath.spi.mapper.MappingException;

public interface Predicate {

    boolean apply(PredicateContext ctx);

    public interface PredicateContext {

        Object item();

        <T> T item(Class<T> clazz) throws MappingException;

        Object root();

        Configuration configuration();
    }
}
