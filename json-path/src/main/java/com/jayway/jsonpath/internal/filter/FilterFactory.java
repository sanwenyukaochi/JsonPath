package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.internal.PathToken;

public class FilterFactory {

    private static final PathTokenFilter DOCUMENT_FILTER = new PassthroughFilter("$", false);
    private static final PathTokenFilter ALL_ARRAY_ITEMS_FILTER =
            new PassthroughFilter("[*]", true);
    private static final PathTokenFilter WILDCARD_FILTER = new WildcardFilter("*");
    private static final PathTokenFilter SCAN_FILTER = new ScanFilter("..");
    private static final PathTokenFilter ARRAY_QUERY_FILTER = new ArrayQueryFilter("[?]");

    public static PathTokenFilter createFilter(PathToken token) {
        String pathFragment = token.getFragment();

        if (DOCUMENT_FILTER.getCondition().equals(pathFragment) && token.isRootToken()) {

            return DOCUMENT_FILTER;

        } else if (ALL_ARRAY_ITEMS_FILTER.getCondition().equals(pathFragment)) {

            return ALL_ARRAY_ITEMS_FILTER;

        } else if ("*".equals(pathFragment) || "['*']".equals(pathFragment)) {

            return WILDCARD_FILTER;

        } else if (SCAN_FILTER.getCondition().equals(pathFragment)) {

            return SCAN_FILTER;

        } else if (ARRAY_QUERY_FILTER.getCondition().equals(pathFragment)) {

            return ARRAY_QUERY_FILTER;

        } else if (!pathFragment.contains("[")) {

            return new FieldFilter(pathFragment);

        } else if (pathFragment.contains("[")) {

            if (pathFragment.startsWith("[?")) {
                if (!pathFragment.contains("=")
                        && !pathFragment.contains("<")
                        && !pathFragment.contains(">")) {

                    return new HasFieldFilter(pathFragment);
                } else {

                    return new ArrayEvalFilter(pathFragment);
                }
            } else {

                return new ArrayIndexFilter(pathFragment);
            }
        }

        throw new UnsupportedOperationException(
                "can not find filter for path fragment " + pathFragment);
    }
}
