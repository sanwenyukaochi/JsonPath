package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.spi.JsonProvider;

import java.util.List;
import java.util.Map;

public class HasFieldFilter extends PathTokenFilter {

    public HasFieldFilter(String condition) {
        super(condition);
    }

    @Override
    public Object filter(Object obj, JsonProvider jsonProvider) {

        List<Object> src = jsonProvider.toList(obj);
        List<Object> result = jsonProvider.createList();

        String trimmedCondition = condition;

        if (condition.contains("['")) {
            trimmedCondition = trimmedCondition.replace("['", ".");
            trimmedCondition = trimmedCondition.replace("']", "");
        }

        trimmedCondition = trim(trimmedCondition, 5, 2);

        for (Object item : src) {
            if (jsonProvider.isMap(item)) {
                Map<String, Object> map = jsonProvider.toMap(item);
                if (map.containsKey(trimmedCondition)) {
                    result.add(map);
                }
            }
        }
        return result;
    }

    @Override
    public Object getRef(Object obj, JsonProvider jsonProvider) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isArrayFilter() {
        return true;
    }
}
