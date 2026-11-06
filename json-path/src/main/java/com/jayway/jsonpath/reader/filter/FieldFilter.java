package com.jayway.jsonpath.reader.filter;

import java.util.LinkedList;
import java.util.List;

public class FieldFilter extends Filter {

    public FieldFilter(String condition) {
        super(condition);
    }

    public Object filter(Object obj) {
        if (isList(obj)) {
            List<Object> result = new LinkedList<Object>();
            for (Object item : toList(obj)) {
                result.add(filter(item));
            }
            return result;
        } else {
            return getMapValue(obj, condition);
        }
    }
}
