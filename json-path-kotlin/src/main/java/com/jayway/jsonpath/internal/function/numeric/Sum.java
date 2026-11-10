package com.jayway.jsonpath.internal.function.numeric;

public class Sum extends AbstractAggregation {
    private Double summation = 0d;

    @Override
    protected void next(Number value) {
        summation += value.doubleValue();
    }

    @Override
    protected Number getValue() {
        return summation;
    }
}
