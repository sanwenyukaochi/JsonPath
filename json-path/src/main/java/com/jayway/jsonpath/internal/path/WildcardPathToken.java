package com.jayway.jsonpath.internal.path;

import static java.util.Arrays.asList;

import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.internal.PathRef;

public class WildcardPathToken extends PathToken {

    WildcardPathToken() {}

    @Override
    public void evaluate(
            String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {
        if (ctx.jsonProvider().isMap(model)) {
            for (String property : ctx.jsonProvider().getPropertyKeys(model)) {
                handleObjectProperty(currentPath, model, ctx, asList(property));
            }
        } else if (ctx.jsonProvider().isArray(model)) {
            for (int idx = 0; idx < ctx.jsonProvider().length(model); idx++) {
                try {
                    handleArrayIndex(idx, currentPath, model, ctx);
                } catch (PathNotFoundException p) {
                    if (ctx.options().contains(Option.REQUIRE_PROPERTIES)) {
                        throw p;
                    }
                }
            }
        }
    }

    @Override
    public boolean isTokenDefinite() {
        return false;
    }

    @Override
    public String getPathFragment() {
        return "[*]";
    }
}
