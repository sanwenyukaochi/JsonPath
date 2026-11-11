package com.jayway.jsonpath.internal.path
import com.jayway.jsonpath.Option
import com.jayway.jsonpath.internal.PathRef
import com.jayway.jsonpath.spi.json.JsonProvider

open class ScanPathToken : PathToken() {
    override fun evaluate(
        currentPath: String,
        parent: PathRef,
        model: Any?,
        ctx: EvaluationContextImpl,
    ) {
        val pt = next()
        walk(pt, currentPath, parent, model, ctx, createScanPredicate(pt, ctx))
    }

    override fun isTokenDefinite(): Boolean = false

    override fun getPathFragment(): String = ".."

    interface Predicate {
        fun matches(model: Any?): Boolean
    }

    private class FilterPathTokenPredicate(
        target: PathToken,
        private val ctx: EvaluationContextImpl,
    ) : Predicate {
        private var predicatePathToken = target as PredicatePathToken

        override fun matches(model: Any?): Boolean = predicatePathToken.accept(model, ctx.rootDocument(), ctx.configuration(), ctx)
    }

    private class WildcardPathTokenPredicate : Predicate {
        override fun matches(model: Any?): Boolean = true
    }

    private class ArrayPathTokenPredicate(
        private val ctx: EvaluationContextImpl,
    ) : Predicate {
        override fun matches(model: Any?): Boolean = ctx.jsonProvider().isArray(model)
    }

    private class PropertyPathTokenPredicate(
        target: PathToken,
        private val ctx: EvaluationContextImpl,
    ) : Predicate {
        private var propertyPathToken = target as PropertyPathToken

        override fun matches(model: Any?): Boolean {
            if (!ctx.jsonProvider().isMap(model)) return false
            if (!propertyPathToken.isTokenDefinite()) return true
            if (propertyPathToken.isLeaf() && ctx.options().contains(Option.DEFAULT_PATH_LEAF_TO_NULL)) return true
            val keys = ctx.jsonProvider().getPropertyKeys(model)
            return keys.containsAll(propertyPathToken.getProperties())
        }
    }

    companion object {
        private val FALSE_PREDICATE =
            object : Predicate {
                override fun matches(model: Any?): Boolean = false
            }

        @JvmStatic fun walk(
            pt: PathToken,
            currentPath: String,
            parent: PathRef,
            model: Any?,
            ctx: EvaluationContextImpl,
            predicate: Predicate,
        ) {
            if (ctx.jsonProvider().isMap(model)) {
                walkObject(pt, currentPath, parent, model, ctx, predicate)
            } else if (ctx.jsonProvider().isArray(model)) {
                walkArray(pt, currentPath, parent, model, ctx, predicate)
            }
        }

        @JvmStatic fun walkArray(
            pt: PathToken,
            currentPath: String,
            parent: PathRef,
            model: Any?,
            ctx: EvaluationContextImpl,
            predicate: Predicate,
        ) {
            if (predicate.matches(model)) {
                if (pt.isLeaf()) {
                    pt.evaluate(currentPath, parent, model, ctx)
                } else {
                    val next = pt.next()
                    val models = ctx.jsonProvider().toIterable(model)
                    var idx = 0
                    for (evalModel in models) {
                        val evalPath =
                            currentPath + "[" + idx + "]"
                        next.setUpstreamArrayIndex(idx)
                        next.evaluate(evalPath, parent, evalModel, ctx)
                        idx++
                    }
                }
            }
            val models = ctx.jsonProvider().toIterable(model)
            var idx = 0
            for (evalModel in models) {
                val evalPath =
                    currentPath + "[" + idx + "]"
                walk(pt, evalPath, PathRef.create(model, idx), evalModel, ctx, predicate)
                idx++
            }
        }

        @JvmStatic fun walkObject(
            pt: PathToken,
            currentPath: String,
            parent: PathRef,
            model: Any?,
            ctx: EvaluationContextImpl,
            predicate: Predicate,
        ) {
            if (predicate.matches(model)) pt.evaluate(currentPath, parent, model, ctx)
            val properties = ctx.jsonProvider().getPropertyKeys(model)
            for (property in properties) {
                val evalPath = currentPath + "['" + property + "']"
                val propertyModel = ctx.jsonProvider().getMapValue(model, property)
                if (propertyModel !==
                    JsonProvider.UNDEFINED
                ) {
                    walk(pt, evalPath, PathRef.create(model, property), propertyModel, ctx, predicate)
                }
            }
        }

        @JvmStatic private fun createScanPredicate(
            target: PathToken,
            ctx: EvaluationContextImpl,
        ): Predicate =
            when (target) {
                is PropertyPathToken -> PropertyPathTokenPredicate(target, ctx)
                is ArrayPathToken -> ArrayPathTokenPredicate(ctx)
                is WildcardPathToken -> WildcardPathTokenPredicate()
                is PredicatePathToken -> FilterPathTokenPredicate(target, ctx)
                else -> FALSE_PREDICATE
            }
    }
}
