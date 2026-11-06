package com.jayway.jsonpath.reader.filter;

import java.util.LinkedList;
import java.util.List;

public class WildcardFilter extends Filter {

    public WildcardFilter(String condition) {
        super(condition);
    }

    @Override
    public Object filter(Object obj) {
        List<Object> result = new LinkedList<Object>();

        if (isList(obj)) {
            for (Object current : toList(obj)) {
                for (Object value : toMap(current).values()) {
                    result.add(value);
                }
            }
        } else {
            for (Object value : toMap(obj).values()) {
                result.add(value);
            }
        }
        return result;
    }
}
