package com.jayway.jsonpath.internal.function.numeric

open class Min : AbstractAggregation() {
    private var min = java.lang.Double.MAX_VALUE

    override fun next(value: Number) {
        if (min > value.toDouble()) min = value.toDouble()
    }

    override fun getValue(): Number = min
}
