package com.jayway.jsonpath.internal.path
import com.jayway.jsonpath.InvalidPathException
import com.jayway.jsonpath.Option
import com.jayway.jsonpath.PathNotFoundException
import com.jayway.jsonpath.internal.PathRef
import com.jayway.jsonpath.internal.Utils

open class PropertyPathToken(
    private val properties: List<@JvmSuppressWildcards String>,
    stringDelimiter: Char,
) : PathToken() {
    private val stringDelimiter: String

    init {
        if (properties.isEmpty()) throw InvalidPathException("Empty properties")
        this.stringDelimiter = stringDelimiter.toString()
    }

    open fun getProperties(): List<String> = properties

    open fun singlePropertyCase(): Boolean = properties.size == 1

    open fun multiPropertyMergeCase(): Boolean = isLeaf() && properties.size > 1

    open fun multiPropertyIterationCase(): Boolean = !isLeaf() && properties.size > 1

    override fun evaluate(
        currentPath: String,
        parent: PathRef,
        model: Any?,
        ctx: EvaluationContextImpl,
    ) {
        if (ASSERTIONS_ENABLED &&
            !Utils.onlyOneIsTrueNonThrow(singlePropertyCase(), multiPropertyMergeCase(), multiPropertyIterationCase())
        ) {
            throw AssertionError()
        }
        if (!ctx.jsonProvider().isMap(model)) {
            if (!isUpstreamDefinite() || ctx.options().contains(Option.SUPPRESS_EXCEPTIONS)) {
                return
            } else {
                val m = model?.javaClass?.name ?: "null"
                throw PathNotFoundException(
                    String.format(
                        "Expected to find an object with property %s in path %s but found '%s'. This is not a json object according to the JsonProvider: '%s'.",
                        getPathFragment(),
                        currentPath,
                        m,
                        ctx
                            .configuration()
                            .jsonProvider()
                            .javaClass.name,
                    ),
                )
            }
        }
        if (singlePropertyCase() || multiPropertyMergeCase()) {
            handleObjectProperty(currentPath, model, ctx, properties)
            return
        }
        if (ASSERTIONS_ENABLED && !multiPropertyIterationCase()) {
            throw AssertionError()
        }
        val currentlyHandledProperty = arrayListOf("")
        for (property in properties) {
            currentlyHandledProperty[0] = property
            handleObjectProperty(currentPath, model, ctx, currentlyHandledProperty)
        }
    }

    override fun isTokenDefinite(): Boolean = singlePropertyCase() || multiPropertyMergeCase()

    override fun getPathFragment(): String =
        StringBuilder()
            .append("[")
            .append(Utils.join(",", stringDelimiter, properties))
            .append("]")
            .toString()

    companion object {
        private val ASSERTIONS_ENABLED = PropertyPathToken::class.java.desiredAssertionStatus()
    }
}
