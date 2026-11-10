package com.jayway.jsonpath.internal.function.text;

import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.Parameter;
import com.jayway.jsonpath.internal.function.PathFunction;
import com.jayway.jsonpath.internal.path.CompiledPath;
import com.jayway.jsonpath.internal.path.PathToken;
import com.jayway.jsonpath.internal.path.RootPathToken;
import com.jayway.jsonpath.internal.path.WildcardPathToken;

import java.util.List;

public class Length implements PathFunction {

    public static final String TOKEN_NAME = "length";

    @Override
    public Object invoke(
            String currentPath,
            PathRef parent,
            Object model,
            EvaluationContext ctx,
            List<Parameter> parameters) {
        if (null != parameters && parameters.size() > 0) {

            Parameter lengthOfParameter = parameters.get(0);
            if (!lengthOfParameter.getPath().isFunctionPath()) {
                Path path = lengthOfParameter.getPath();
                if (path instanceof CompiledPath) {
                    RootPathToken root = ((CompiledPath) path).getRoot();
                    PathToken tail = root.getNext();
                    while (null != tail && null != tail.getNext()) {
                        tail = tail.getNext();
                    }
                    if (null != tail) {
                        tail.setNext(new WildcardPathToken());
                    }
                }
            }
            Object innerModel =
                    parameters
                            .get(0)
                            .getPath()
                            .evaluate(model, model, ctx.configuration())
                            .getValue();
            if (ctx.configuration().jsonProvider().isArray(innerModel)) {
                return ctx.configuration().jsonProvider().length(innerModel);
            }
        }
        if (ctx.configuration().jsonProvider().isArray(model)) {
            return ctx.configuration().jsonProvider().length(model);
        } else if (ctx.configuration().jsonProvider().isMap(model)) {
            return ctx.configuration().jsonProvider().length(model);
        }
        return null;
    }
}
