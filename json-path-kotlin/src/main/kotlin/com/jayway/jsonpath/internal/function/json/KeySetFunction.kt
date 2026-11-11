package com.jayway.jsonpath.internal.function.json
import com.jayway.jsonpath.internal.EvaluationContext
import com.jayway.jsonpath.internal.PathRef
import com.jayway.jsonpath.internal.function.Parameter
import com.jayway.jsonpath.internal.function.PathFunction

open class KeySetFunction : PathFunction {
    override fun invoke(
        currentPath: String?,
        parent: PathRef?,
        model: Any?,
        ctx: EvaluationContext,
        parameters: List<@JvmSuppressWildcards Parameter>?,
    ): Any? {
        if (ctx.configuration().jsonProvider().isMap(model)) return ctx.configuration().jsonProvider().getPropertyKeys(model)
        return null
    }
}
