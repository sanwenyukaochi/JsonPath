package com.jayway.jsonpath.internal

open class EvaluationAbortException : RuntimeException() {
    override fun fillInStackTrace(): Throwable = this

    private companion object {
        private const val serialVersionUID = 4419305302960432348L
    }
}
