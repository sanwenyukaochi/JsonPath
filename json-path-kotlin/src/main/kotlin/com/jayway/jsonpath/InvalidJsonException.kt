package com.jayway.jsonpath

open class InvalidJsonException : JsonPathException {
    private val jsonValue: String?

    constructor() : super() {
        jsonValue = null
    }

    constructor(message: String?) : super(message) {
        jsonValue = null
    }

    constructor(
        message: String?,
        cause: Throwable?,
    ) : super(message, cause) {
        jsonValue = null
    }

    constructor(cause: Throwable?) : super(cause) {
        jsonValue = null
    }

    constructor(
        cause: Throwable?,
        json: String?,
    ) : super(cause) {
        jsonValue = json
    }

    open fun getJson(): String? = jsonValue
}
