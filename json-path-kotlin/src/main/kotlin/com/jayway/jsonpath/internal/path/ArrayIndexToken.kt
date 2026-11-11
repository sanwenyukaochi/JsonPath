package com.jayway.jsonpath.internal.path
import com.jayway.jsonpath.internal.PathRef

open class ArrayIndexToken(
    private val arrayIndexOperation: ArrayIndexOperation,
) : ArrayPathToken() {
    override fun evaluate(
        currentPath: String,
        parent: PathRef,
        model: Any?,
        ctx: EvaluationContextImpl,
    ) {
        if (!checkArrayModel(currentPath, model, ctx)) return
        if (arrayIndexOperation.isSingleIndexOperation()) {
            handleArrayIndex(arrayIndexOperation.indexes()[0], currentPath, model, ctx)
        } else {
            for (index in arrayIndexOperation.indexes()) handleArrayIndex(index, currentPath, model, ctx)
        }
    }

    override fun getPathFragment(): String = arrayIndexOperation.toString()

    override fun isTokenDefinite(): Boolean = arrayIndexOperation.isSingleIndexOperation()
}
