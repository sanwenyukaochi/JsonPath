package com.jayway.jsonpath

open class ValueCompareException : JsonPathException {
    constructor() : super()

    constructor(
        left: Any?,
        right: Any?,
    ) : super(
        String.format(
            "Can not compare a %1s with a %2s",
            left?.javaClass?.name ?: "null",
            right?.javaClass?.name ?: "null",
        ),
    )

    constructor(message: String?) : super(message)

    constructor(
        message: String?,
        cause: Throwable?,
    ) : super(message, cause)
}
