package com.jayway.jsonpath.filter;

import java.util.regex.Pattern;

public class JsonPathFilterFactory {

    private static final Pattern ROOT_FILTER_PATTERN = Pattern.compile("\\$");
    private static final Pattern PROPERTY_FILTER_PATTERN = Pattern.compile("(.*)|\\['(.*?)'\\]");
    private static final Pattern WILDCARD_PROPERTY_FILTER_PATTERN = Pattern.compile("\\*");
    private static final Pattern LIST_FILTER_PATTERN = Pattern.compile("\\[.*?\\]");
    private static final Pattern TRAVERSE_FILTER_PATTERN = Pattern.compile("\\.\\.");

    private static final RootFilter ROOT_FILTER = new RootFilter();
    private static final TraverseFilter TRAVERSE_FILTER = new TraverseFilter();

    public static JsonPathFilterBase createFilter(String pathFragment) {

        if (ROOT_FILTER_PATTERN.matcher(pathFragment).matches()) {
            return ROOT_FILTER;
        } else if (LIST_FILTER_PATTERN.matcher(pathFragment).matches()) {
            return new ListFilter(pathFragment);
        } else if (TRAVERSE_FILTER_PATTERN.matcher(pathFragment).matches()) {
            return TRAVERSE_FILTER;
        } else if (PROPERTY_FILTER_PATTERN.matcher(pathFragment).matches()
                || WILDCARD_PROPERTY_FILTER_PATTERN.matcher(pathFragment).matches()) {
            return new PropertyFilter(pathFragment);
        }

        return null;
    }
}
