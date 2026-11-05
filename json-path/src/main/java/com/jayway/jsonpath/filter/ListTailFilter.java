package com.jayway.jsonpath.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListTailFilter extends JsonPathFilterBase {

    private static final Pattern LIST_TAIL_PATTERN_SHORT =
            Pattern.compile("\\[\\s*-\\s*(\\d+):\\s*\\]");
    private static final Pattern LIST_TAIL_PATTERN_LONG =
            Pattern.compile("\\[\\s*\\(\\s*@\\.length\\s*-\\s*(\\d+)\\s*\\)\\s*\\]");

    public static final Pattern PATTERN =
            Pattern.compile(
                    "("
                            + LIST_TAIL_PATTERN_SHORT.pattern()
                            + "|"
                            + LIST_TAIL_PATTERN_LONG.pattern()
                            + ")");

    private final String pathFragment;

    public ListTailFilter(String pathFragment) {
        this.pathFragment = pathFragment;
    }

    @Override
    public FilterOutput apply(FilterOutput filterItems) {

        int index = getTailIndex(filterItems.getResultAsList().size());

        return new FilterOutput(filterItems.getResultAsList().get(index));
    }

    private int getTailIndex(int arraySize) {

        Matcher matcher = LIST_TAIL_PATTERN_SHORT.matcher(pathFragment);
        if (matcher.matches()) {

            int index = Integer.parseInt(matcher.group(1));

            return arraySize - index;
        }
        matcher = LIST_TAIL_PATTERN_LONG.matcher(pathFragment);
        if (matcher.matches()) {

            int index = Integer.parseInt(matcher.group(1));

            return arraySize - index;
        }

        throw new IllegalArgumentException("invalid list index");
    }
}
