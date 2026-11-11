package com.jayway.jsonpath

fun interface MapFunction {
    fun map(
        currentValue: Any?,
        configuration: Configuration,
    ): Any?
}
