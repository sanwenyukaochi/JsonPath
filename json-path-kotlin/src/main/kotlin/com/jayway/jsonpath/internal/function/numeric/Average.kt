package com.jayway.jsonpath.internal.function.numeric

open class Average : AbstractAggregation() {
    private var summation = 0.0
    private var count = 0.0

    override fun next(value: Number) {
        count++
        summation += value.toDouble()
    }

    override fun getValue(): Number {
        if (count != 0.0) return summation / count
        return 0.0
    }
}
