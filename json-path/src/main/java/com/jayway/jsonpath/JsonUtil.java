package com.jayway.jsonpath;

import java.util.List;
import java.util.Map;

public class JsonUtil {

    public static boolean isContainer(Object obj) {
        return isList(obj) || isMap(obj);
    }

    public static boolean isList(Object obj) {
        return obj instanceof List;
    }

    public static boolean isMap(Object obj) {
        return obj instanceof Map;
    }

    public static List<Object> toList(Object obj) {
        return (List<Object>) obj;
    }

    public static Map<Object, Object> toMap(Object obj) {
        return (Map<Object, Object>) obj;
    }
}
