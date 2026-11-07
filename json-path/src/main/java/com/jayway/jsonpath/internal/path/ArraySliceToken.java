package com.jayway.jsonpath.internal.path;

import com.jayway.jsonpath.internal.PathRef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArraySliceToken extends ArrayPathToken {

    private static final Logger logger = LoggerFactory.getLogger(ArraySliceToken.class);

    private final ArraySliceOperation operation;

    ArraySliceToken(final ArraySliceOperation operation) {
        this.operation = operation;
    }

    @Override
    public void evaluate(
            String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {
        if (!checkArrayModel(currentPath, model, ctx)) return;
        switch (operation.operation()) {
            case SLICE_FROM:
                sliceFrom(currentPath, parent, model, ctx);
                break;
            case SLICE_BETWEEN:
                sliceBetween(currentPath, parent, model, ctx);
                break;
            case SLICE_TO:
                sliceTo(currentPath, parent, model, ctx);
                break;
        }
    }

    private void sliceFrom(
            String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {
        int length = ctx.jsonProvider().length(model);
        int from = operation.from();
        if (from < 0) {

            from = length + from;
        }
        from = Math.max(0, from);

        logger.debug(
                "Slice from index on array with length: {}. From index: {} to: {}. Input: {}",
                length,
                from,
                length - 1,
                toString());

        if (length == 0 || from >= length) {
            return;
        }
        for (int i = from; i < length; i++) {
            handleArrayIndex(i, currentPath, model, ctx);
        }
    }

    private void sliceBetween(
            String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {
        int length = ctx.jsonProvider().length(model);
        int from = operation.from();
        int to = operation.to();

        to = Math.min(length, to);

        if (from >= to || length == 0) {
            return;
        }

        logger.debug(
                "Slice between indexes on array with length: {}. From index: {} to: {}. Input: {}",
                length,
                from,
                to,
                toString());

        for (int i = from; i < to; i++) {
            handleArrayIndex(i, currentPath, model, ctx);
        }
    }

    private void sliceTo(
            String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {
        int length = ctx.jsonProvider().length(model);
        if (length == 0) {
            return;
        }
        int to = operation.to();
        if (to < 0) {

            to = length + to;
        }
        to = Math.min(length, to);

        logger.debug(
                "Slice to index on array with length: {}. From index: 0 to: {}. Input: {}",
                length,
                to,
                toString());

        for (int i = 0; i < to; i++) {
            handleArrayIndex(i, currentPath, model, ctx);
        }
    }

    @Override
    public String getPathFragment() {
        return operation.toString();
    }

    @Override
    public boolean isTokenDefinite() {
        return false;
    }
}
