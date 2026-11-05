package com.jayway.jsonpath.filter;

import com.jayway.jsonpath.JsonUtil;
import java.util.List;
import java.util.regex.Pattern;
import org.json.simple.JSONArray;

public class WildcardPropertyFilter extends JsonPathFilterBase {

    public static final Pattern PATTERN = Pattern.compile("\\*");

    @Override
    public FilterOutput apply(FilterOutput filter) {

        List<Object> result = new JSONArray();

        if (filter.isList()) {
            for (Object current : filter.getResultAsList()) {
                for (Object value : JsonUtil.toMap(current).values()) {
                    result.add(value);
                }
            }
        } else {
            for (Object value : JsonUtil.toMap(filter.getResult()).values()) {
                result.add(value);
            }
        }
        return new FilterOutput(result);
    }
}
