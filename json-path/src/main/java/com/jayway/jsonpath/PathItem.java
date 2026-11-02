package com.jayway.jsonpath;

public class PathItem {

    private final String path;

    private final Object target;

    public PathItem(String path, Object target) {
        this.path = path;
        this.target = target;
    }

    public String getPath() {
        return path;
    }

    public Object getTarget() {
        return target;
    }


}
