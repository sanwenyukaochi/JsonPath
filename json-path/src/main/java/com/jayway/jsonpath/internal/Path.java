package com.jayway.jsonpath.internal;

import com.jayway.jsonpath.Configuration;

public interface Path {

    EvaluationContext evaluate(Object document, Object rootDocument, Configuration configuration);

    EvaluationContext evaluate(
            Object document, Object rootDocument, Configuration configuration, boolean forUpdate);

    boolean isDefinite();

    boolean isRootPath();
}
