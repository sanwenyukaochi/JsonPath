package com.jayway.jsonpath

fun interface EvaluationListener {
    fun resultFound(found: FoundResult): EvaluationContinuation

    enum class EvaluationContinuation {
        CONTINUE,
        ABORT,
    }

    interface FoundResult {
        fun index(): Int

        fun path(): String

        fun result(): Any?
    }
}
