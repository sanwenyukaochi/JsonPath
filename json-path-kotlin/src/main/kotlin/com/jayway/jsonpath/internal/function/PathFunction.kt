package com.jayway.jsonpath.internal.function
import com.jayway.jsonpath.internal.EvaluationContext
import com.jayway.jsonpath.internal.PathRef

interface PathFunction {
    fun invoke(
        currentPath: String?,
        parent: PathRef?,
        model: Any?,
        ctx: EvaluationContext,
        parameters: List<@JvmSuppressWildcards Parameter>?,
    ): Any?
}
