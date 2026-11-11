package com.jayway.jsonpath.internal.path
import com.jayway.jsonpath.Option
import com.jayway.jsonpath.PathNotFoundException
import com.jayway.jsonpath.internal.PathRef

open class WildcardPathToken : PathToken() {
    override fun evaluate(
        currentPath: String,
        parent: PathRef,
        model: Any?,
        ctx: EvaluationContextImpl,
    ) {
        if (ctx.jsonProvider().isMap(model)) {
            for (property in ctx.jsonProvider().getPropertyKeys(
                model,
            )) {
                handleObjectProperty(currentPath, model, ctx, java.util.Collections.singletonList(property))
            }
        } else if (ctx.jsonProvider().isArray(model)) {
            var idx = 0
            while (idx < ctx.jsonProvider().length(model)) {
                try {
                    handleArrayIndex(idx, currentPath, model, ctx)
                } catch (
                    p: PathNotFoundException,
                ) {
                    if (ctx.options().contains(Option.REQUIRE_PROPERTIES)) throw p
                }
                idx++
            }
        }
    }

    override fun isTokenDefinite(): Boolean = false

    override fun getPathFragment(): String = "[*]"
}
