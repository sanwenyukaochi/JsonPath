package com.jayway.jsonpath.spi.mapper

import com.jayway.jsonpath.JsonPathException

open class MappingException : JsonPathException {
    constructor(cause: Throwable?) : super(cause)

    constructor(message: String?) : super(message)
}
