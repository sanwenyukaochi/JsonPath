package com.jayway.jsonpath.filter;

import com.jayway.jsonpath.JsonUtil;
import java.util.List;
import org.json.simple.JSONArray;

public class TraverseFilter extends JsonPathFilterBase {

    @Override
    public List<Object> apply(List<Object> filter) {
        List<Object> result = new JSONArray();

        traverse(filter, result);

        return result;
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
