package com.jayway.jsonpath.internal.path

internal interface Path {
    fun evaluate(rootDocument: Any): Any?
}
