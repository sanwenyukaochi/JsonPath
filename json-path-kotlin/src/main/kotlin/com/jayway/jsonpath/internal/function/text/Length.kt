package com.jayway.jsonpath.internal.function.text
import com.jayway.jsonpath.internal.EvaluationContext
import com.jayway.jsonpath.internal.PathRef
import com.jayway.jsonpath.internal.function.Parameter
import com.jayway.jsonpath.internal.function.PathFunction
import com.jayway.jsonpath.internal.path.CompiledPath
import com.jayway.jsonpath.internal.path.WildcardPathToken

open class Length : PathFunction {
    override fun invoke(
        currentPath: String?,
        parent: PathRef?,
        model: Any?,
        ctx: EvaluationContext,
        parameters: List<@JvmSuppressWildcards Parameter>?,
    ): Any? {
        if (parameters != null && parameters.size > 0) {
            val lengthOfParameter = parameters[0]
            if (!requireNotNull(lengthOfParameter.getPath()) { "length() argument must be a path" }.isFunctionPath()) {
                val path = lengthOfParameter.getPath()
                if (path is CompiledPath) {
                    val root = path.getRoot()
                    var tail = root.getNext()
                    while (tail != null && tail.getNext() != null) tail = tail.getNext()
                    if (tail != null) tail.setNext(WildcardPathToken())
                }
            }
            val innerModel: Any? =
                requireNotNull(parameters[0].getPath()) {
                    "length() argument must be a path"
                }.evaluate(model, model, ctx.configuration()).getValue()
            if (ctx.configuration().jsonProvider().isArray(innerModel)) return ctx.configuration().jsonProvider().length(innerModel)
        }
        if (ctx.configuration().jsonProvider().isArray(model)) {
            return ctx.configuration().jsonProvider().length(model)
        } else if (ctx.configuration().jsonProvider().isMap(model)) {
            return ctx.configuration().jsonProvider().length(model)
        }
        return null
    }

    companion object {
        const val TOKEN_NAME: String = "length"
    }
}
