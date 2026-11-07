package com.jayway.jsonpath.internal.path;

import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.Parameter;
import com.jayway.jsonpath.internal.function.PathFunction;
import com.jayway.jsonpath.internal.function.PathFunctionFactory;
import com.jayway.jsonpath.internal.function.latebinding.JsonLateBindingValue;
import com.jayway.jsonpath.internal.function.latebinding.PathLateBindingValue;

import java.util.List;

public class FunctionPathToken extends PathToken {

    private final String functionName;
    private final String pathFragment;
    private List<Parameter> functionParams;

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
                            param.setLateBinding(
                                    new PathLateBindingValue(
                                            param.getPath(),
                                            ctx.rootDocument(),
                                            ctx.configuration()));
                            param.setEvaluated(true);
                            break;
                        case JSON:
                            param.setLateBinding(
                                    new JsonLateBindingValue(
                                            ctx.configuration().jsonProvider(), param));
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

    public void setParameters(List<Parameter> parameters) {
        this.functionParams = parameters;
    }
}
