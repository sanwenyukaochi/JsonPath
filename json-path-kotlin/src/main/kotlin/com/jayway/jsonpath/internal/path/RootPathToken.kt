package com.jayway.jsonpath.internal.path
import com.jayway.jsonpath.internal.PathRef

open class RootPathToken(
    rootToken: Char,
) : PathToken() {
    private var tail: PathToken = this
    private var tokenCount = 1
    private val rootToken = rootToken.toString()

    open fun getTail(): PathToken = tail

    override fun getTokenCount(): Int = tokenCount

    open fun append(next: PathToken): RootPathToken {
        tail = tail.appendTailToken(next)
        tokenCount++
        return this
    }

    open fun getPathTokenAppender(): PathTokenAppender =
        object : PathTokenAppender {
            override fun appendPathToken(next: PathToken): PathTokenAppender {
                append(next)
                return this
            }
        }

    override fun evaluate(
        currentPath: String,
        parent: PathRef,
        model: Any?,
        ctx: EvaluationContextImpl,
    ) {
        if (isLeaf()) {
            val op = if (ctx.forUpdate()) parent else PathRef.NO_OP
            ctx.addResult(rootToken, op, model)
        } else {
            next().evaluate(rootToken, parent, model, ctx)
        }
    }

    override fun getPathFragment(): String = rootToken

    override fun isTokenDefinite(): Boolean = true

    open fun isFunctionPath(): Boolean = tail is FunctionPathToken

    open fun setTail(token: PathToken) {
        tail = token
    }
}
