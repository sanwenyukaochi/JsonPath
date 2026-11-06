package com.jayway.jsonpath.reader.filter;

public class FilterFactory {

    public static Filter createFilter(String pathFragment) {

        if ("$".equals(pathFragment) || "[*]".equals(pathFragment)) {

            return new PassThrewFilter(pathFragment);

        } else if ("*".equals(pathFragment)) {

            return new WildcardFilter(pathFragment);

        } else if (pathFragment.contains("..")) {

            return new ScanFilter(pathFragment);

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
