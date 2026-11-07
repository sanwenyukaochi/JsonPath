package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.spi.JsonProvider;

public class HasPathFilter extends PathTokenFilter {

    private final JsonPath path;

    public HasPathFilter(String condition) {
        super(condition);
        String trimmedCondition = condition;

        if (condition.contains("['")) {
            trimmedCondition = trimmedCondition.replace("['", ".");
            trimmedCondition = trimmedCondition.replace("']", "");
        }

        this.path = JsonPath.compile(trim(trimmedCondition, 5, 2));
    }

    @Override
    public Object filter(Object obj, Configuration configuration) {
        JsonProvider jsonProvider = configuration.getProvider();

        Iterable<Object> src = jsonProvider.toIterable(obj);
        Object result = jsonProvider.createArray();

        for (Object item : src) {
            if (jsonProvider.isMap(item)) {
                try {
                    path.read(
                            item,
                            Configuration.builder()
                                    .options(Option.THROW_ON_MISSING_PROPERTY)
                                    .jsonProvider(jsonProvider)
                                    .build());
                    jsonProvider.setProperty(result, jsonProvider.length(result), item);
                } catch (PathNotFoundException e) {
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
