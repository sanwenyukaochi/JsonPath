package com.jayway.jsonpath.internal.function.numeric

open class StandardDeviation : AbstractAggregation() {
    private var sumSq = 0.0
    private var sum = 0.0
    private var count = 0.0

    override fun next(value: Number) {
        sum += value.toDouble()
        sumSq += value.toDouble() * value.toDouble()
        count++
    }

    override fun getValue(): Number = Math.sqrt(sumSq / count - sum * sum / count / count)
}
