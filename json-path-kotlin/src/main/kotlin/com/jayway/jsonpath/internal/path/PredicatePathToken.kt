package com.jayway.jsonpath.internal.path
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.InvalidPathException
import com.jayway.jsonpath.Predicate
import com.jayway.jsonpath.internal.PathRef

open class PredicatePathToken : PathToken {
    private val predicates: Collection<Predicate>
    constructor(filter: Predicate) {
        predicates = java.util.Collections.singletonList(filter)
    }
    constructor(predicates: Collection<@JvmSuppressWildcards Predicate>) {
        this.predicates = predicates
    }

    override fun evaluate(
        currentPath: String,
        parent: PathRef,
        model: Any?,
        ctx: EvaluationContextImpl,
    ) {
        if (ctx.jsonProvider().isMap(model)) {
            if (accept(model, ctx.rootDocument(), ctx.configuration(), ctx)) {
                val op = if (ctx.forUpdate()) parent else PathRef.NO_OP
                if (isLeaf()) ctx.addResult(currentPath, op, model) else next().evaluate(currentPath, op, model, ctx)
            }
        } else if (ctx.jsonProvider().isArray(model)) {
            var idx = 0
            val objects = ctx.jsonProvider().toIterable(model)
            for (idxModel in objects) {
                if (accept(idxModel, ctx.rootDocument(), ctx.configuration(), ctx)) handleArrayIndex(idx, currentPath, model, ctx)
                idx++
            }
        } else if (isUpstreamDefinite()) {
            throw InvalidPathException(
                String.format("Filter: %s can not be applied to primitives. Current context is: %s", toString(), model),
            )
        }
    }

    open fun accept(
        obj: Any?,
        root: Any?,
        configuration: Configuration,
        evaluationContext: EvaluationContextImpl,
    ): Boolean {
        val ctx: Predicate.PredicateContext = PredicateContextImpl(obj, root, configuration, evaluationContext.documentEvalCache())
        for (predicate in predicates) {
            try {
                if (!predicate.apply(ctx)) return false
            } catch (_: InvalidPathException) {
                return false
            }
        }
        return true
    }

    override fun getPathFragment(): String {
        val sb = StringBuilder()
        sb.append("[")
        for (i in predicates.indices) {
            if (i != 0) sb.append(",")
            sb.append("?")
        }
        sb.append("]")
        return sb.toString()
    }

    override fun isTokenDefinite(): Boolean = false
}
