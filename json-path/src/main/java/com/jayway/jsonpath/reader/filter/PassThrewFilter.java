package com.jayway.jsonpath.reader.filter;

import com.jayway.jsonpath.spi.JsonProvider;

public class PassThrewFilter extends Filter {

    private boolean isArrayFilter;

    public PassThrewFilter(String condition, boolean isArrayFilter) {
        super(condition);
        this.isArrayFilter = isArrayFilter;
    }

    public Object filter(Object obj, JsonProvider jsonProvider) {
        return obj;
    }

    @Override
    public boolean isArrayFilter() {
        return isArrayFilter;
    }
}
