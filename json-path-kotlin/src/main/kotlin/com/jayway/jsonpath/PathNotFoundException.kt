package com.jayway.jsonpath

open class PathNotFoundException : InvalidPathException {
    constructor() : super()

    constructor(message: String?) : super(message)

    constructor(
        message: String?,
        cause: Throwable?,
    ) : super(message, cause)

    constructor(cause: Throwable?) : super(cause)

    @Synchronized
    override fun fillInStackTrace(): Throwable = this
}
