package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.PathRef;

public interface Function {

    Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx);
}
