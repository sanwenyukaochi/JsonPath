package com.jayway.jsonpath.internal.function.numeric

open class Max : AbstractAggregation() {
    private var max = java.lang.Double.MIN_VALUE

    override fun next(value: Number) {
        if (max < value.toDouble()) max = value.toDouble()
    }

    override fun getValue(): Number = max
}
