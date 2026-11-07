package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.JsonProvider;

public class WildcardFilter extends PathTokenFilter {

    public WildcardFilter(String condition) {
        super(condition);
    }

    @Override
    public Object filter(Object obj, Configuration configuration) {
        JsonProvider jsonProvider = configuration.getProvider();
        Object result = jsonProvider.createArray();

        if (jsonProvider.isArray(obj)) {
            for (Object current : jsonProvider.toIterable(obj)) {
                for (Object value : jsonProvider.toIterable(current)) {
                    jsonProvider.setProperty(result, jsonProvider.length(result), value);
                }
            }
        } else {
            for (Object value : jsonProvider.toIterable(obj)) {
                jsonProvider.setProperty(result, jsonProvider.length(result), value);
            }
        }
        return result;
    }

    @Override
    public Object getRef(Object obj, Configuration configuration) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isArrayFilter() {
        return true;
    }
}
