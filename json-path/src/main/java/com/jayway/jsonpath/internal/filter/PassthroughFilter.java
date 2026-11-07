package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.spi.JsonProvider;

public class PassthroughFilter extends PathTokenFilter {

    private boolean isArrayFilter;

    public PassthroughFilter(String condition, boolean isArrayFilter) {
        super(condition);
        this.isArrayFilter = isArrayFilter;
    }

    public Object filter(Object obj, JsonProvider jsonProvider) {
        return obj;
    }

    @Override
    public Object getRef(Object obj, JsonProvider jsonProvider) {
        return obj;
    }

    @Override
    public boolean isArrayFilter() {
        return isArrayFilter;
    }
}
