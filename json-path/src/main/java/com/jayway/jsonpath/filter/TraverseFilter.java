package com.jayway.jsonpath.filter;

import com.jayway.jsonpath.JsonUtil;
import java.util.List;
import java.util.regex.Pattern;
import org.json.simple.JSONArray;

public class TraverseFilter extends JsonPathFilterBase {

    public static final Pattern PATTERN = Pattern.compile("\\.\\.");

    @Override
    public FilterOutput apply(FilterOutput filter) {
        List<Object> result = new JSONArray();

        traverse(filter.getResult(), result);

        return new FilterOutput(result);
    }

    private void traverse(Object container, List<Object> result) {

        if (JsonUtil.isMap(container)) {
            result.add(container);

            for (Object value : JsonUtil.toMap(container).values()) {
                if (JsonUtil.isContainer(value)) {
                    traverse(value, result);
                }
            }
        } else if (JsonUtil.isList(container)) {

            for (Object value : JsonUtil.toList(container)) {
                if (JsonUtil.isContainer(value)) {
                    traverse(value, result);
                }
            }
        }
    }
}
