package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.spi.JsonProvider;

public abstract class Filter {

    final String condition;

    Filter(String condition) {
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

    public Object filter(Object obj, JsonProvider jsonProvider, boolean inArrayContext) {
        return filter(obj, jsonProvider);
    }

    public abstract Object filter(Object obj, JsonProvider jsonProvider);

    public abstract boolean isArrayFilter();
}
