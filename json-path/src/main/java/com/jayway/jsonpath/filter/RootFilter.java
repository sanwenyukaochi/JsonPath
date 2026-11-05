package com.jayway.jsonpath.filter;

import java.util.regex.Pattern;

public class RootFilter extends JsonPathFilterBase {

    public static final Pattern PATTERN = Pattern.compile("\\$");

    @Override
    public FilterOutput apply(FilterOutput root) {
        return root;
    }
}
