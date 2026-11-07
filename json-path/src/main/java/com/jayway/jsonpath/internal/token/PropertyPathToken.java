package com.jayway.jsonpath.internal.token;

import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.Utils;

import java.util.List;

class PropertyPathToken extends PathToken {

    private final List<String> properties;

    public PropertyPathToken(List<String> properties) {
        this.properties = properties;
    }

    public List<String> getProperties() {
        return properties;
    }

    @Override
    public void evaluate(
            String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {
        if (!ctx.jsonProvider().isMap(model)) {

            String m = model == null ? "null" : model.getClass().getName();

            throw new PathNotFoundException(
                    "Expected to find an object with property "
                            + getPathFragment()
                            + " but found '"
                            + m
                            + "'. This is not a json object according to the JsonProvider: '"
                            + ctx.configuration().jsonProvider().getClass().getName()
                            + "'.");
        }

        handleObjectProperty(currentPath, model, ctx, properties);
    }

    @Override
    public boolean isTokenDefinite() {
        return true;
    }

    @Override
    public String getPathFragment() {
        return new StringBuilder()
                .append("[")
                .append(Utils.join(",", "'", properties))
                .append("]")
                .toString();
    }
}
