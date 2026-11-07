package com.jayway.jsonpath.internal;

import com.jayway.jsonpath.internal.filter.Filter;
import com.jayway.jsonpath.internal.filter.FilterFactory;
import com.jayway.jsonpath.spi.JsonProvider;

public class PathToken {

    private String fragment;

    public PathToken(String fragment) {
        this.fragment = fragment;
    }

    public Filter getFilter() {
        return FilterFactory.createFilter(fragment);
    }

    public Object filter(Object model, JsonProvider jsonProvider) {
        return FilterFactory.createFilter(fragment).filter(model, jsonProvider);
    }

    public Object apply(Object model, JsonProvider jsonProvider) {
        return FilterFactory.createFilter(fragment).getRef(model, jsonProvider);
    }

    public String getFragment() {
        return fragment;
    }
}
