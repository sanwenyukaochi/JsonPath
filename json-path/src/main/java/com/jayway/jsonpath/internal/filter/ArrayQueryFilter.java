package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Filter;

import java.util.LinkedList;

public class ArrayQueryFilter extends PathTokenFilter {

    ArrayQueryFilter(String condition) {
        super(condition);
    }

    @Override
    public Object filter(
            Object obj,
            Configuration configuration,
            LinkedList<Filter> filters,
            boolean inArrayContext) {
        Filter filter = filters.poll();
        return filter.doFilter(configuration.getProvider().toIterable(obj), configuration);
    }

    @Override
    public Object filter(Object obj, Configuration configuration) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getRef(Object obj, Configuration configuration) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public boolean isArrayFilter() {
        return true;
    }
}
