package com.jayway.jsonpath.internal.function.text
import com.jayway.jsonpath.internal.EvaluationContext
import com.jayway.jsonpath.internal.PathRef
import com.jayway.jsonpath.internal.function.Parameter
import com.jayway.jsonpath.internal.function.PathFunction

open class Concatenate : PathFunction {
    override fun invoke(
        currentPath: String?,
        parent: PathRef?,
        model: Any?,
        ctx: EvaluationContext,
        parameters: List<@JvmSuppressWildcards Parameter>?,
    ): Any? {
        val result = StringBuilder()
        if (ctx.configuration().jsonProvider().isArray(model)) {
            val objects = ctx.configuration().jsonProvider().toIterable(model)
            for (obj in objects) if (obj is String) result.append(obj)
        }
        if (parameters != null) for (value in Parameter.toList(String::class.java, ctx, parameters)) result.append(value)
        return result.toString()
    }
}
