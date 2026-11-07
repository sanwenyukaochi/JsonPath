package com.jayway.jsonpath.internal.token;

import com.jayway.jsonpath.Function;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.FunctionFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionPathToken extends PathToken {

    private final String functionName;
    private final String pathFragment;

    public FunctionPathToken(String pathFragment) {
        this.pathFragment = pathFragment;
        Matcher matcher = Pattern.compile(".*?\\%(\\w+)\\(.*?").matcher(pathFragment);
        if (matcher.matches()) {
            functionName = matcher.group(1);
        } else {

            functionName = null;
        }
    }

    @Override
    public void evaluate(
            String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {
        Function function = FunctionFactory.newFunction(functionName);
        Object result = function.invoke(currentPath, parent, model, ctx);
        ctx.addResult(currentPath, parent, result);
    }

    @Override
    public boolean isTokenDefinite() {
        return true;
    }

    @Override
    public String getPathFragment() {
        return pathFragment;
    }
}
