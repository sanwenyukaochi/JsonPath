package com.jayway.jsonpath.internal.path;

import static java.lang.String.format;

import com.jayway.jsonpath.PathNotFoundException;

public abstract class ArrayPathToken extends PathToken {

    protected boolean checkArrayModel(String currentPath, Object model, EvaluationContextImpl ctx) {
        if (model == null) {
            if (!isUpstreamDefinite()) {
                return false;
            } else {
                throw new PathNotFoundException("The path " + currentPath + " is null");
            }
        }
        if (!ctx.jsonProvider().isArray(model)) {
            if (!isUpstreamDefinite()) {
                return false;
            } else {
                throw new PathNotFoundException(
                        format(
                                "Filter: %s can only be applied to arrays. Current context is: %s",
                                toString(), model));
            }
        }
        return true;
    }
}
