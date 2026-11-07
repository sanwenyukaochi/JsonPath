package com.jayway.jsonpath.internal.path;

import com.jayway.jsonpath.internal.PathRef;

public class ArrayIndexToken extends ArrayPathToken {

    private final ArrayIndexOperation arrayIndexOperation;

    ArrayIndexToken(final ArrayIndexOperation arrayIndexOperation) {
        this.arrayIndexOperation = arrayIndexOperation;
    }

    @Override
    public void evaluate(
            String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {
        if (!checkArrayModel(currentPath, model, ctx)) return;
        if (arrayIndexOperation.isSingleIndexOperation()) {
            handleArrayIndex(arrayIndexOperation.indexes().get(0), currentPath, model, ctx);
        } else {
            for (Integer index : arrayIndexOperation.indexes()) {
                handleArrayIndex(index, currentPath, model, ctx);
            }
        }
    }

    @Override
    public String getPathFragment() {
        return arrayIndexOperation.toString();
    }

    @Override
    public boolean isTokenDefinite() {
        return arrayIndexOperation.isSingleIndexOperation();
    }
}
