package com.jayway.jsonpath.internal.path
import com.jayway.jsonpath.Option
import com.jayway.jsonpath.PathNotFoundException

abstract class ArrayPathToken : PathToken() {
    protected open fun checkArrayModel(
        currentPath: String,
        model: Any?,
        ctx: EvaluationContextImpl,
    ): Boolean {
        if (model == null) {
            if (!isUpstreamDefinite() || ctx.options().contains(Option.SUPPRESS_EXCEPTIONS)) {
                return false
            } else {
                throw PathNotFoundException("The path " + currentPath + " is null")
            }
        }
        if (!ctx.jsonProvider().isArray(model)) {
            if (!isUpstreamDefinite() || ctx.options().contains(Option.SUPPRESS_EXCEPTIONS)) {
                return false
            } else {
                throw PathNotFoundException(
                    String.format("Filter: %s can only be applied to arrays. Current context is: %s", toString(), model),
                )
            }
        }
        return true
    }
}
