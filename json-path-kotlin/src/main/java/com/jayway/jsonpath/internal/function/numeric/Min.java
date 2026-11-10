package com.jayway.jsonpath.internal.function.numeric;

public class Min extends AbstractAggregation {
    private Double min = Double.MAX_VALUE;

    @Override
    protected void next(Number value) {
        if (min > value.doubleValue()) {
            min = value.doubleValue();
        }
    }

    @Override
    protected Number getValue() {
        return min;
    }
}
