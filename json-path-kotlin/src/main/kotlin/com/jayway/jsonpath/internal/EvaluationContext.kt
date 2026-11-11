package com.jayway.jsonpath.internal
import com.jayway.jsonpath.Configuration

interface EvaluationContext {
    fun configuration(): Configuration

    fun rootDocument(): Any?

    fun <T> getValue(): T?

    fun <T> getValue(unwrap: Boolean): T?

    fun <T> getPath(): T?

    fun getPathList(): List<String>

    fun updateOperations(): Collection<PathRef>
}
