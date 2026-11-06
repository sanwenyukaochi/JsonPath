package com.jayway.jsonpath.reader.filter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HasFieldFilter extends Filter {

    public HasFieldFilter(String condition) {
        super(condition);
    }

    @Override
    public Object filter(Object obj) {

        List<Object> src = toList(obj);
        List<Object> result = new LinkedList<Object>();

        String trimmedCondition = trim(condition, 5, 2);

        for (Object item : src) {
            if (isMap(item)) {
                Map<String, Object> map = toMap(item);
                if (map.containsKey(trimmedCondition)) {
                    result.add(map);
                }
            }
        }
        return result;
    }
}
