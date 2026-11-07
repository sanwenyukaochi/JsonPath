package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Filter;

import java.util.LinkedList;

public abstract class PathTokenFilter {

    final String condition;

    PathTokenFilter(String condition) {
        this.condition = condition;
    }

    String getCondition() {
        return condition;
    }

    static String trim(String str, int front, int end) {
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
            Configuration configuration,
            LinkedList<Filter> filters,
            boolean inArrayContext) {
        return filter(obj, configuration);
    }

    public abstract Object filter(Object obj, Configuration configuration);

    public abstract Object getRef(Object obj, Configuration configuration);

    public abstract boolean isArrayFilter();

    @Override
    public String toString() {
        if (isArrayFilter()) {
            return getClass().getSimpleName() + " => " + condition;
        } else {
            return getClass().getSimpleName() + " => '" + condition + "'";
        }
    }
}
