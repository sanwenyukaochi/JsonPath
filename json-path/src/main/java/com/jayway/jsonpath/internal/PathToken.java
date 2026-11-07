package com.jayway.jsonpath.internal;

import com.jayway.jsonpath.InvalidModelException;
import com.jayway.jsonpath.internal.filter.FilterFactory;
import com.jayway.jsonpath.internal.filter.PathTokenFilter;
import com.jayway.jsonpath.spi.JsonProvider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathToken {

    private static final Pattern ARRAY_INDEX_PATTERN = Pattern.compile("\\[(\\d+)\\]");

    private String fragment;

    private int tokenIndex;

    public PathToken(String fragment, int tokenIndex) {
        this.fragment = fragment;
        this.tokenIndex = tokenIndex;
    }

    public PathTokenFilter getFilter() {
        return FilterFactory.createFilter(this);
    }

    public Object filter(Object model, JsonProvider jsonProvider) {
        return FilterFactory.createFilter(this).filter(model, jsonProvider);
    }

    public Object apply(Object model, JsonProvider jsonProvider) {
        return FilterFactory.createFilter(this).getRef(model, jsonProvider);
    }

    public String getFragment() {
        return fragment;
    }

    public boolean isRootToken() {
        return this.tokenIndex == 0;
    }

    public boolean isArrayIndexToken() {
        return ARRAY_INDEX_PATTERN.matcher(fragment).matches();
    }

    public int getArrayIndex() {
        Matcher matcher = ARRAY_INDEX_PATTERN.matcher(fragment);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        } else
            throw new InvalidModelException("Could not get array index from fragment " + fragment);
    }
}
