package com.jayway.jsonpath.internal.function.json
import com.jayway.jsonpath.internal.EvaluationContext
import com.jayway.jsonpath.internal.PathRef
import com.jayway.jsonpath.internal.function.Parameter
import com.jayway.jsonpath.internal.function.PathFunction

open class Append : PathFunction {
    override fun invoke(
        currentPath: String?,
        parent: PathRef?,
        model: Any?,
        ctx: EvaluationContext,
        parameters: List<@JvmSuppressWildcards Parameter>?,
    ): Any? {
        val jsonProvider = ctx.configuration().jsonProvider()
        if (parameters != null && parameters.size > 0) {
            for (param in parameters) {
                if (jsonProvider.isArray(model)) {
                    val len = jsonProvider.length(model)
                    jsonProvider.setArrayIndex(model, len, param.getValue())
                }
            }
        }
        return model
    }
}
