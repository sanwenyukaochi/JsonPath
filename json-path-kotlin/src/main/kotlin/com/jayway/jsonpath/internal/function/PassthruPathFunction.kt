package com.jayway.jsonpath.internal.function
import com.jayway.jsonpath.internal.EvaluationContext
import com.jayway.jsonpath.internal.PathRef

open class PassthruPathFunction : PathFunction {
    override fun invoke(
        currentPath: String?,
        parent: PathRef?,
        model: Any?,
        ctx: EvaluationContext,
        parameters: List<@JvmSuppressWildcards Parameter>?,
    ): Any? = model
}
