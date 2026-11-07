package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.Configuration;

public class PassthroughFilter extends PathTokenFilter {

    private boolean isArrayFilter;

    public PassthroughFilter(String condition, boolean isArrayFilter) {
        super(condition);
        this.isArrayFilter = isArrayFilter;
    }

    @Override
    public Object filter(Object obj, Configuration configuration) {
        return obj;
    }

    @Override
    public Object getRef(Object obj, Configuration configuration) {
        return obj;
    }

    @Override
    public boolean isArrayFilter() {
        return isArrayFilter;
    }
}
