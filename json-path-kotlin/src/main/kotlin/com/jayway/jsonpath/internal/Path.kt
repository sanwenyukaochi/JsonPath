package com.jayway.jsonpath.internal
import com.jayway.jsonpath.Configuration

interface Path {
    fun evaluate(
        document: Any?,
        rootDocument: Any?,
        configuration: Configuration,
    ): EvaluationContext

    fun evaluate(
        document: Any?,
        rootDocument: Any?,
        configuration: Configuration,
        forUpdate: Boolean,
    ): EvaluationContext

    fun isDefinite(): Boolean

    fun isFunctionPath(): Boolean

    fun isRootPath(): Boolean
}
