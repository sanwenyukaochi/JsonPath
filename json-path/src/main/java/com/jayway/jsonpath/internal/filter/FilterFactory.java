package com.jayway.jsonpath.internal.filter;

public class FilterFactory {

    private static final PathTokenFilter DOCUMENT_FILTER = new PassthroughFilter("$", false);
    private static final PathTokenFilter ALL_ARRAY_ITEMS_FILTER =
            new PassthroughFilter("[*]", true);
    private static final PathTokenFilter WILDCARD_FILTER = new WildcardFilter("*");
    private static final PathTokenFilter SCAN_FILTER = new ScanFilter("..");

    public static PathTokenFilter createFilter(String pathFragment) {

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

        } else if (pathFragment.equals("[?]")) {

            return new ArrayQueryFilter(pathFragment);

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
