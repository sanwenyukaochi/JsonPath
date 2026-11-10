package com.jayway.jsonpath.internal.function.numeric;

public class Average extends AbstractAggregation {

    private Double summation = 0d;
    private Double count = 0d;

    @Override
    protected void next(Number value) {
        count++;
        summation += value.doubleValue();
    }

    @Override
    protected Number getValue() {
        if (count != 0d) {
            return summation / count;
        }
        return 0d;
    }
}
