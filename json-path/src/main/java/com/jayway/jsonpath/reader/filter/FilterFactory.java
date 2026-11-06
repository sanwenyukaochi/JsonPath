package com.jayway.jsonpath.reader.filter;

public class FilterFactory {

    private static final Filter DOCUMENT_FILTER = new PassThroughFilter("$", false);
    private static final Filter ALL_ARRAY_ITEMS_FILTER = new PassThroughFilter("[*]", true);
    private static final Filter WILDCARD_FILTER = new WildcardFilter("*");
    private static final Filter SCAN_FILTER = new ScanFilter("..");

    public static Filter createFilter(String pathFragment) {

        if ("$".equals(pathFragment)) {

            return DOCUMENT_FILTER;

        } else if ("[*]".equals(pathFragment)) {

            return ALL_ARRAY_ITEMS_FILTER;

        } else if ("*".equals(pathFragment) || "['*']".equals(pathFragment)) {

            return WILDCARD_FILTER;

        } else if (pathFragment.contains("..")) {

            return SCAN_FILTER;

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

        throw new UnsupportedOperationException("..");
    }
}
