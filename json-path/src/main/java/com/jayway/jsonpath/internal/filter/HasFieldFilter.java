package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.JsonProvider;

import java.util.Collection;

public class HasFieldFilter extends PathTokenFilter {

    private final String trimmedCondition;

    public HasFieldFilter(String condition) {
        super(condition);
        String trimmedCondition = condition;

        if (condition.contains("['")) {
            trimmedCondition = trimmedCondition.replace("['", ".");
            trimmedCondition = trimmedCondition.replace("']", "");
        }

        this.trimmedCondition = trim(trimmedCondition, 5, 2);
    }

    @Override
    public Object filter(Object obj, Configuration configuration) {
        JsonProvider jsonProvider = configuration.getProvider();

        Iterable<Object> src = jsonProvider.toIterable(obj);
        Object result = jsonProvider.createArray();

        for (Object item : src) {
            if (jsonProvider.isMap(item)) {
                Collection<String> keys = jsonProvider.getPropertyKeys(item);
                if (keys.contains(trimmedCondition)) {
                    jsonProvider.setProperty(result, jsonProvider.length(result), item);
                }
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
