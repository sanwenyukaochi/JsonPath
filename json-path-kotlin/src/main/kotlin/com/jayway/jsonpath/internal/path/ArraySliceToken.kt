package com.jayway.jsonpath.internal.path
import com.jayway.jsonpath.internal.PathRef
import org.slf4j.LoggerFactory

open class ArraySliceToken(
    private val operation: ArraySliceOperation,
) : ArrayPathToken() {
    override fun evaluate(
        currentPath: String,
        parent: PathRef,
        model: Any?,
        ctx: EvaluationContextImpl,
    ) {
        if (!checkArrayModel(currentPath, model, ctx)) return
        when (operation.operation()) {
            ArraySliceOperation.Operation.SLICE_FROM -> sliceFrom(currentPath, parent, model, ctx)
            ArraySliceOperation.Operation.SLICE_BETWEEN -> sliceBetween(currentPath, parent, model, ctx)
            ArraySliceOperation.Operation.SLICE_TO -> sliceTo(currentPath, parent, model, ctx)
        }
    }

    private fun sliceFrom(
        currentPath: String,
        parent: PathRef,
        model: Any?,
        ctx: EvaluationContextImpl,
    ) {
        val length = ctx.jsonProvider().length(model)
        var from = checkNotNull(operation.from()) { "Slice operation requires a start index" }
        if (from < 0) from = length + from
        from = Math.max(0, from)
        logger.debug("Slice from index on array with length: {}. From index: {} to: {}. Input: {}", length, from, length - 1, toString())
        if (length == 0 || from >= length) return
        for (i in from until length) handleArrayIndex(i, currentPath, model, ctx)
    }

    private fun sliceBetween(
        currentPath: String,
        parent: PathRef,
        model: Any?,
        ctx: EvaluationContextImpl,
    ) {
        val length = ctx.jsonProvider().length(model)
        val from = checkNotNull(operation.from()) { "Slice operation requires a start index" }
        var to = checkNotNull(operation.to()) { "Slice operation requires an end index" }
        to = Math.min(length, to)
        if (from >= to || length == 0) return
        logger.debug("Slice between indexes on array with length: {}. From index: {} to: {}. Input: {}", length, from, to, toString())
        for (i in from until to) handleArrayIndex(i, currentPath, model, ctx)
    }

    private fun sliceTo(
        currentPath: String,
        parent: PathRef,
        model: Any?,
        ctx: EvaluationContextImpl,
    ) {
        val length = ctx.jsonProvider().length(model)
        if (length == 0) return
        var to = checkNotNull(operation.to()) { "Slice operation requires an end index" }
        if (to < 0) to = length + to
        to = Math.min(length, to)
        logger.debug("Slice to index on array with length: {}. From index: 0 to: {}. Input: {}", length, to, toString())
        for (i in 0 until to) handleArrayIndex(i, currentPath, model, ctx)
    }

    override fun getPathFragment(): String = operation.toString()

    override fun isTokenDefinite(): Boolean = false

    companion object {
        private val logger = LoggerFactory.getLogger(ArraySliceToken::class.java)
    }
}
