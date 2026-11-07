package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.spi.JsonProvider;
import java.util.LinkedList;

public class ArrayQueryFilter extends PathTokenFilter {

    ArrayQueryFilter(String condition) {
        super(condition);
    }

    @Override
    public Object filter(
            Object obj,
            JsonProvider jsonProvider,
            LinkedList<Filter> filters,
            boolean inArrayContext) {

        Filter filter = filters.poll();

        return filter.doFilter(jsonProvider.toIterable(obj), jsonProvider);
    }

    @Override
    public Object filter(Object obj, JsonProvider jsonProvider) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getRef(Object obj, JsonProvider jsonProvider) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public boolean isArrayFilter() {
        return true;
    }
}
