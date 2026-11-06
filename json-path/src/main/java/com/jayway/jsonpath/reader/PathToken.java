package com.jayway.jsonpath.reader;

import com.jayway.jsonpath.reader.filter.Filter;
import com.jayway.jsonpath.reader.filter.FilterFactory;
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

    public String getFragment() {
        return fragment;
    }
}
