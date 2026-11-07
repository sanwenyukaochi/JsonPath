package com.jayway.jsonpath.internal.token;

import com.jayway.jsonpath.internal.PathRef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RootPathToken extends PathToken {

    private static final Logger logger = LoggerFactory.getLogger(RootPathToken.class);

    private PathToken tail;
    private int tokenCount;

    public RootPathToken() {
        this.tail = this;
        this.tokenCount = 1;
    }

    @Override
    public int getTokenCount() {
        return tokenCount;
    }

    public RootPathToken append(PathToken next) {
        this.tail = tail.appendTailToken(next);
        this.tokenCount++;
        return this;
    }

    @Override
    public void evaluate(
            String currentPath, PathRef pathRef, Object model, EvaluationContextImpl ctx) {
        if (isLeaf()) {
            PathRef op = ctx.forUpdate() ? pathRef : PathRef.NO_OP;
            ctx.addResult("$", op, model);
        } else {
            next().evaluate("$", pathRef, model, ctx);
        }
    }

    @Override
    public String getPathFragment() {
        return "$";
    }

    @Override
    boolean isTokenDefinite() {
        return true;
    }
}
