package com.jayway.jsonpath.internal.function.sequence;

import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.function.Parameter;

import java.util.List;

public class Index extends AbstractSequenceAggregation {
    @Override
    protected int targetIndex(EvaluationContext ctx, List<Parameter> parameters) {
        return getIndexFromParameters(ctx, parameters);
    }
}
