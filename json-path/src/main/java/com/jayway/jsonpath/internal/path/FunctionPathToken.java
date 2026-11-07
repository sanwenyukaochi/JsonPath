package com.jayway.jsonpath.internal.path;

import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.Parameter;
import com.jayway.jsonpath.internal.function.PathFunction;
import com.jayway.jsonpath.internal.function.PathFunctionFactory;

import java.util.List;

public class FunctionPathToken extends PathToken {

    private final String functionName;
    private final String pathFragment;
    private final List<Parameter> functionParams;

    public FunctionPathToken(String pathFragment, List<Parameter> parameters) {
        this.pathFragment =
                pathFragment + ((parameters != null && parameters.size() > 0) ? "(...)" : "()");
        if (null != pathFragment) {
            functionName = pathFragment;
            functionParams = parameters;
        } else {
            functionName = null;
            functionParams = null;
        }
    }

    @Override
    public void evaluate(
            String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {
        PathFunction pathFunction = PathFunctionFactory.newFunction(functionName);
        evaluateParameters(currentPath, parent, model, ctx);
        Object result = pathFunction.invoke(currentPath, parent, model, ctx, functionParams);
        ctx.addResult(currentPath + "." + functionName, parent, result);
        if (!isLeaf()) {
            next().evaluate(currentPath, parent, result, ctx);
        }
    }

    private void evaluateParameters(
            String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {

        if (null != functionParams) {
            for (Parameter param : functionParams) {
                if (!param.hasEvaluated()) {
                    switch (param.getType()) {
                        case PATH:
                            param.setCachedValue(
                                    param.getPath()
                                            .evaluate(
                                                    ctx.rootDocument(),
                                                    ctx.rootDocument(),
                                                    ctx.configuration())
                                            .getValue());
                            param.setEvaluated(true);
                            break;
                        case JSON:
                            param.setCachedValue(
                                    ctx.configuration().jsonProvider().parse(param.getJson()));
                            param.setEvaluated(true);
                            break;
                    }
                }
            }
        }
    }

    @Override
    public boolean isTokenDefinite() {
        return true;
    }

    @Override
    public String getPathFragment() {
        return "." + pathFragment;
    }
}
