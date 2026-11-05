package com.jayway.jsonpath.filter;

import com.jayway.jsonpath.JsonUtil;
import com.jayway.jsonpath.eval.ExpressionEvaluator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minidev.json.JSONArray;

public class ListEvalFilter extends JsonPathFilterBase {

    public static final Pattern PATTERN =
            Pattern.compile("\\[\\s?\\?\\s?\\(\\s?@.(\\w+)\\s?([=<>]+)\\s?(.*)\\s?\\)\\s?\\]");

    private final String pathFragment;

    public ListEvalFilter(String pathFragment) {
        this.pathFragment = pathFragment;
    }

    @Override
    public FilterOutput apply(FilterOutput filterItems) {

        List<Object> result = new JSONArray();

        for (Object item : filterItems.getResultAsList()) {
            if (isMatch(item)) {
                result.add(item);
            }
        }
        return new FilterOutput(result);
    }

    private boolean isMatch(Object check) {
        Matcher matcher = PATTERN.matcher(pathFragment);

        if (matcher.matches()) {
            String property = matcher.group(1);
            String operator = matcher.group(2);
            String expected = matcher.group(3);

            if (!JsonUtil.isMap(check)) {
                return false;
            }
            Map obj = JsonUtil.toMap(check);

            if (!obj.containsKey(property)) {
                return false;
            }

            Object propertyValue = obj.get(property);

            if (JsonUtil.isContainer(propertyValue)) {
                return false;
            }

            String expression = propertyValue + " " + operator + " " + expected;

            return ExpressionEvaluator.eval(propertyValue, operator, expected);
        }
        return false;
    }
}
