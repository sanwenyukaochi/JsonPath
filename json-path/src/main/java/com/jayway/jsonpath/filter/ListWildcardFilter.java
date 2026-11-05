package com.jayway.jsonpath.filter;

import java.util.regex.Pattern;

public class ListWildcardFilter extends JsonPathFilterBase {

    public static final Pattern PATTERN = Pattern.compile("\\[\\*\\]");

    @Override
    public FilterOutput apply(FilterOutput filterItems) {
        return new FilterOutput(filterItems.getResultAsList());
    }
}
