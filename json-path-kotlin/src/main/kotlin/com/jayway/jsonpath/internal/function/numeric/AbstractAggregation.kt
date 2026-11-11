package com.jayway.jsonpath.internal.function.numeric
import com.jayway.jsonpath.JsonPathException
import com.jayway.jsonpath.internal.EvaluationContext
import com.jayway.jsonpath.internal.PathRef
import com.jayway.jsonpath.internal.function.Parameter
import com.jayway.jsonpath.internal.function.PathFunction

abstract class AbstractAggregation : PathFunction {
    protected abstract fun next(value: Number)

    protected abstract fun getValue(): Number

    override fun invoke(
        currentPath: String?,
        parent: PathRef?,
        model: Any?,
        ctx: EvaluationContext,
        parameters: List<@JvmSuppressWildcards Parameter>?,
    ): Any? {
        var count = 0
        if (ctx.configuration().jsonProvider().isArray(model)) {
            val objects = ctx.configuration().jsonProvider().toIterable(model)
            for (obj in objects) {
                if (obj is Number) {
                    count++
                    next(obj)
                }
            }
        }
        if (parameters != null) {
            for (value in Parameter.toList(Number::class.java, ctx, parameters)) {
                count++
                next(value)
            }
        }
        if (count != 0) return getValue()
        throw JsonPathException("Aggregation function attempted to calculate value using empty array")
    }
}
