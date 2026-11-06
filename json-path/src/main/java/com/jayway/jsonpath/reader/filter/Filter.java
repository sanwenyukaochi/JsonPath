package com.jayway.jsonpath.reader.filter;

import java.util.List;
import java.util.Map;

public abstract class Filter {

    protected final String condition;

    public Filter(String condition) {
        this.condition = condition;
    }

    protected boolean isContainer(Object obj) {
        return (isList(obj) || isMap(obj));
    }

    protected boolean isList(Object obj) {
        return (obj instanceof List);
    }

    protected List<Object> toList(Object list) {
        return (List<Object>) list;
    }

    protected Map<String, Object> toMap(Object map) {
        return (Map<String, Object>) map;
    }

    protected Object getMapValue(Object obj, String key) {
        Map<String, Object> map = toMap(obj);
        return map.get(key);
    }

    protected boolean isMap(Object obj) {
        return (obj instanceof Map);
    }

    protected String trim(String str, int front, int end) {
        String res = str;

        if (front > 0) {
            res = str.substring(front);
        }
        if (end > 0) {
            res = res.substring(0, res.length() - end);
        }
        return res;
    }

    public abstract Object filter(Object obj);
}
