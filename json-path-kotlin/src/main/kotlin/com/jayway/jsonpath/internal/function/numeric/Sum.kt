package com.jayway.jsonpath.internal.function.numeric

open class Sum : AbstractAggregation() {
    private var summation = 0.0

    override fun next(value: Number) {
        summation += value.toDouble()
    }

    override fun getValue(): Number = summation
}
