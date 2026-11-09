package com.jayway.jsonpath

open class JsonPathException : RuntimeException {
    constructor(message: String) : super(message)

    constructor(cause: Throwable) : super(cause)
}
