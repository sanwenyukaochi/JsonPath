package com.jayway.jsonpath.internal.function.sequence
import com.jayway.jsonpath.internal.EvaluationContext
import com.jayway.jsonpath.internal.function.Parameter

open class Index : AbstractSequenceAggregation() {
    override fun targetIndex(
        ctx: EvaluationContext,
        parameters: List<@JvmSuppressWildcards Parameter>?,
    ): Int = getIndexFromParameters(ctx, parameters)
}
