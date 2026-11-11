package com.jayway.jsonpath.internal.function.sequence
import com.jayway.jsonpath.JsonPathException
import com.jayway.jsonpath.internal.EvaluationContext
import com.jayway.jsonpath.internal.PathRef
import com.jayway.jsonpath.internal.function.Parameter
import com.jayway.jsonpath.internal.function.PathFunction

abstract class AbstractSequenceAggregation : PathFunction {
    protected abstract fun targetIndex(
        ctx: EvaluationContext,
        parameters: List<@JvmSuppressWildcards Parameter>?,
    ): Int

    override fun invoke(
        currentPath: String?,
        parent: PathRef?,
        model: Any?,
        ctx: EvaluationContext,
        parameters: List<@JvmSuppressWildcards Parameter>?,
    ): Any? {
        if (ctx.configuration().jsonProvider().isArray(model)) {
            val objects = ctx.configuration().jsonProvider().toIterable(model)
            val objectList = ArrayList<Any?>()
            objects.forEach(objectList::add)
            val targetIndex = targetIndex(ctx, parameters)
            if (targetIndex >= 0) {
                return objectList[targetIndex]
            } else {
                val realIndex = objectList.size + targetIndex
                if (realIndex > 0) {
                    return objectList[realIndex]
                } else {
                    throw JsonPathException("Target index:" + targetIndex + " larger than object count:" + objectList.size)
                }
            }
        }
        throw JsonPathException("Aggregation function attempted to calculate value using empty array")
    }

    protected open fun getIndexFromParameters(
        ctx: EvaluationContext,
        parameters: List<@JvmSuppressWildcards Parameter>?,
    ): Int {
        val numbers = Parameter.toList(Number::class.java, ctx, parameters)
        return numbers[0].toInt()
    }
}
