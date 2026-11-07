package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.spi.JsonProvider;

import java.util.LinkedList;

public abstract class PathTokenFilter {

    final String condition;

    PathTokenFilter(String condition) {
        this.condition = condition;
    }

    String trim(String str, int front, int end) {
        String res = str;

        if (front > 0) {
            res = str.substring(front);
        }
        if (end > 0) {
            res = res.substring(0, res.length() - end);
        }
        return res;
    }

    public Object filter(
            Object obj,
            JsonProvider jsonProvider,
            LinkedList<Filter> filters,
            boolean inArrayContext) {
        return filter(obj, jsonProvider);
    }

    public abstract Object filter(Object obj, JsonProvider jsonProvider);

    public abstract Object getRef(Object obj, JsonProvider jsonProvider);

    public abstract boolean isArrayFilter();
}
