package com.jayway.jsonpath.internal;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.InvalidModelException;
import com.jayway.jsonpath.internal.filter.FilterFactory;
import com.jayway.jsonpath.internal.filter.PathTokenFilter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathToken {

    private static final Pattern ARRAY_INDEX_PATTERN = Pattern.compile("\\[(\\d+)]");

    private final String fragment;

    private final int tokenIndex;

    private final boolean endToken;

    public PathToken(String fragment, int tokenIndex, boolean isEndToken) {
        this.fragment = fragment;
        this.tokenIndex = tokenIndex;
        this.endToken = isEndToken;
    }

    public PathTokenFilter getFilter() {
        return FilterFactory.createFilter(this);
    }

    public Object filter(Object model, Configuration configuration) {
        return FilterFactory.createFilter(this).filter(model, configuration);
    }

    public Object apply(Object model, Configuration configuration) {
        return FilterFactory.createFilter(this).getRef(model, configuration);
    }

    public String getFragment() {
        return fragment;
    }

    public boolean isRootToken() {
        return this.tokenIndex == 0;
    }

    public boolean isEndToken() {
        return this.endToken;
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
