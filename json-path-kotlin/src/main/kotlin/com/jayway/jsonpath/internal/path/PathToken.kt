package com.jayway.jsonpath.internal.path
import com.jayway.jsonpath.Option
import com.jayway.jsonpath.PathNotFoundException
import com.jayway.jsonpath.internal.PathRef
import com.jayway.jsonpath.internal.Utils
import com.jayway.jsonpath.internal.function.PathFunction
import com.jayway.jsonpath.spi.json.JsonProvider

abstract class PathToken {
    private var prev: PathToken? = null
    private var next: PathToken? = null
    private var definite: Boolean? = null
    private var upstreamDefinite: Boolean? = null
    private var upstreamArrayIndex = -1

    open fun setUpstreamArrayIndex(idx: Int) {
        upstreamArrayIndex = idx
    }

    open fun appendTailToken(next: PathToken): PathToken {
        this.next = next
        next.prev = this
        return next
    }

    open fun handleObjectProperty(
        currentPath: String,
        model: Any?,
        ctx: EvaluationContextImpl,
        properties: List<String>,
    ) {
        if (properties.size == 1) {
            val property = properties[0]
            val evalPath = Utils.concat(currentPath, "['", property, "']")
            var propertyVal = readObjectProperty(property, model, ctx)
            if (propertyVal === JsonProvider.UNDEFINED) {
                if (ASSERTIONS_ENABLED && this !is PropertyPathToken) {
                    throw AssertionError("only PropertyPathToken is supported")
                }
                if (isLeaf()) {
                    if (ctx.options().contains(Option.DEFAULT_PATH_LEAF_TO_NULL)) {
                        propertyVal = null
                    } else {
                        if (ctx.options().contains(Option.SUPPRESS_EXCEPTIONS) || !ctx.options().contains(Option.REQUIRE_PROPERTIES)) {
                            return
                        } else {
                            throw PathNotFoundException("No results for path: " + evalPath)
                        }
                    }
                } else {
                    if ((!(isUpstreamDefinite() && isTokenDefinite()) && !ctx.options().contains(Option.REQUIRE_PROPERTIES)) ||
                        ctx.options().contains(Option.SUPPRESS_EXCEPTIONS)
                    ) {
                        return
                    } else {
                        throw PathNotFoundException("Missing property in path " + evalPath)
                    }
                }
            }
            val pathRef = if (ctx.forUpdate()) PathRef.create(model, property) else PathRef.NO_OP
            if (isLeaf()) {
                val idx = "[" + upstreamArrayIndex.toString() + "]"
                if (idx == "[-1]" ||
                    checkNotNull(ctx.getRoot().getTail().prev()) { "Path tail must have a predecessor" }
                        .getPathFragment() == idx
                ) {
                    ctx.addResult(evalPath, pathRef, propertyVal)
                }
            } else {
                next().evaluate(evalPath, pathRef, propertyVal, ctx)
            }
        } else {
            val evalPath = currentPath + "[" + Utils.join(", ", "'", properties) + "]"
            if (ASSERTIONS_ENABLED && !isLeaf()) {
                throw AssertionError("non-leaf multi props handled elsewhere")
            }
            val merged = ctx.jsonProvider().createMap()
            for (property in properties) {
                var propertyVal: Any?
                if (hasProperty(property, model, ctx)) {
                    propertyVal = readObjectProperty(property, model, ctx)
                    if (propertyVal === JsonProvider.UNDEFINED) {
                        if (ctx.options().contains(Option.DEFAULT_PATH_LEAF_TO_NULL)) propertyVal = null else continue
                    }
                } else {
                    if (ctx.options().contains(Option.DEFAULT_PATH_LEAF_TO_NULL)) {
                        propertyVal = null
                    } else if (ctx.options().contains(Option.REQUIRE_PROPERTIES)) {
                        throw PathNotFoundException("Missing property in path " + evalPath)
                    } else {
                        continue
                    }
                }
                ctx.jsonProvider().setProperty(merged, property, propertyVal)
            }
            val pathRef = if (ctx.forUpdate()) PathRef.create(model, properties) else PathRef.NO_OP
            ctx.addResult(evalPath, pathRef, merged)
        }
    }

    protected open fun handleArrayIndex(
        index: Int,
        currentPath: String,
        model: Any?,
        ctx: EvaluationContextImpl,
    ) {
        val evalPath = Utils.concat(currentPath, "[", index.toString(), "]")
        val pathRef = if (ctx.forUpdate()) PathRef.create(model, index) else PathRef.NO_OP
        val effectiveIndex = if (index < 0) ctx.jsonProvider().length(model) + index else index
        try {
            val evalHit = ctx.jsonProvider().getArrayIndex(model, effectiveIndex)
            if (isLeaf()) ctx.addResult(evalPath, pathRef, evalHit) else next().evaluate(evalPath, pathRef, evalHit, ctx)
        } catch (_: IndexOutOfBoundsException) {
        }
    }

    open fun prev(): PathToken? = prev

    open fun next(): PathToken {
        if (isLeaf()) throw IllegalStateException("Current path token is a leaf")
        return checkNotNull(next) { "Current path token is a leaf" }
    }

    open fun isLeaf(): Boolean = next == null

    open fun isRoot(): Boolean = prev == null

    open fun isUpstreamDefinite(): Boolean {
        upstreamDefinite?.let { return it }
        val result =
            if (isRoot()) {
                true
            } else {
                val previous = checkNotNull(prev) { "Non-root token must have a predecessor" }
                previous.isTokenDefinite() && previous.isUpstreamDefinite()
            }
        upstreamDefinite = result
        return result
    }

    open fun getTokenCount(): Int {
        var cnt = 1
        var token: PathToken = this
        while (!token.isLeaf()) {
            token = token.next()
            cnt++
        }
        return cnt
    }

    open fun isPathDefinite(): Boolean {
        definite?.let { return it }
        var isDefinite = isTokenDefinite()
        if (isDefinite && !isLeaf()) isDefinite = next().isPathDefinite()
        definite = isDefinite
        return isDefinite
    }

    override fun toString(): String = if (isLeaf()) getPathFragment() else getPathFragment() + next().toString()

    override fun hashCode(): Int = toString().hashCode()

    override fun equals(other: Any?): Boolean = super.equals(other)

    open fun invoke(
        pathFunction: PathFunction,
        currentPath: String,
        parent: PathRef,
        model: Any?,
        ctx: EvaluationContextImpl,
    ) {
        ctx.addResult(currentPath, parent, pathFunction.invoke(currentPath, parent, model, ctx, null))
    }

    abstract fun evaluate(
        currentPath: String,
        parent: PathRef,
        model: Any?,
        ctx: EvaluationContextImpl,
    )

    abstract fun isTokenDefinite(): Boolean

    abstract fun getPathFragment(): String

    open fun setNext(next: PathToken?) {
        this.next = next
    }

    open fun getNext(): PathToken? = next

    companion object {
        private val ASSERTIONS_ENABLED = PathToken::class.java.desiredAssertionStatus()

        @JvmStatic private fun hasProperty(
            property: String,
            model: Any?,
            ctx: EvaluationContextImpl,
        ): Boolean = ctx.jsonProvider().getPropertyKeys(model).contains(property)

        @JvmStatic private fun readObjectProperty(
            property: String,
            model: Any?,
            ctx: EvaluationContextImpl,
        ): Any? = ctx.jsonProvider().getMapValue(model, property)
    }
}
