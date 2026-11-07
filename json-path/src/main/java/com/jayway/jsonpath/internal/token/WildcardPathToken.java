package com.jayway.jsonpath.internal.token;

import static java.util.Arrays.asList;

import com.jayway.jsonpath.PathNotFoundException;

public class WildcardPathToken extends PathToken {

    @Override
    public void evaluate(String currentPath, Object model, EvaluationContextImpl ctx) {
        if (ctx.jsonProvider().isMap(model)) {
            for (String property : ctx.jsonProvider().getPropertyKeys(model)) {
                handleObjectProperty(currentPath, model, ctx, asList(property));
            }
        } else if (ctx.jsonProvider().isArray(model)) {
            for (int idx = 0; idx < ctx.jsonProvider().length(model); idx++) {
                try {
                    handleArrayIndex(idx, currentPath, model, ctx);
                } catch (PathNotFoundException p) {
                    if (!isLeaf() && !next().isLeaf()) {
                        throw p;
                    }
                }
            }
        }
    }

    @Override
    boolean isTokenDefinite() {
        return false;
    }

    @Override
    public String getPathFragment() {
        return "[*]";
    }
}
