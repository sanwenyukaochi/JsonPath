package com.jayway.jsonpath.internal.function.numeric;

import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.Function;

public abstract class AbstractAggregation implements Function {

    protected abstract void next(Number value);

    protected abstract Number getValue();

    @Override
    public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx) {
        if (ctx.configuration().jsonProvider().isArray(model)) {

            Iterable<?> objects = ctx.configuration().jsonProvider().toIterable(model);
            for (Object obj : objects) {

                if (obj instanceof Number) {
                    Number value = (Number) obj;
                    next(value);
                }
            }
            return getValue();
        }
        return null;
    }
}
