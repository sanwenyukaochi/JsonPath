package com.jayway.jsonpath.filter;

import com.jayway.jsonpath.PathUtil;
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

        if (PathUtil.isDocument(container)) {
            result.add(container);

            for (Object value : PathUtil.toDocument(container).values()) {
                if (PathUtil.isContainer(value)) {
                    traverse(value, result);
                }
            }
        } else if (PathUtil.isArray(container)) {

            for (Object value : PathUtil.toArray(container)) {
                if (PathUtil.isContainer(value)) {
                    traverse(value, result);
                }
            }
        }
    }
}
