package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.spi.JsonProvider;

public class ScanFilter extends PathTokenFilter {

    public ScanFilter(String condition) {
        super(condition);
    }

    @Override
    public Object filter(Object obj, JsonProvider jsonProvider) {
        Object result = jsonProvider.createArray();
        scan(obj, result, jsonProvider);

        return result;
    }

    @Override
    public boolean isArrayFilter() {
        return true;
    }

    @Override
    public Object getRef(Object obj, JsonProvider jsonProvider) {
        throw new UnsupportedOperationException();
    }

    private void scan(Object container, Object result, JsonProvider jsonProvider) {

        if (jsonProvider.isMap(container)) {
            jsonProvider.setProperty(result, jsonProvider.length(result), container);
        }

        for (Object value : jsonProvider.toIterable(container)) {
            if (jsonProvider.isContainer(value)) {
                scan(value, result, jsonProvider);
            }
        }
    }
}
