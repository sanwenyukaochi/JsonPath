package com.jayway.jsonpath.internal.function.numeric;

public class Max extends AbstractAggregation {
    private Double max = Double.MIN_VALUE;

    @Override
    protected void next(Number value) {
        if (max < value.doubleValue()) {
            max = value.doubleValue();
        }
    }

    @Override
    protected Number getValue() {
        return max;
    }
}
