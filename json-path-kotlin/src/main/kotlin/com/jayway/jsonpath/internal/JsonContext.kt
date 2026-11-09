package com.jayway.jsonpath.internal

import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.internal.path.PathCompiler

internal class JsonContext(
    private val jsonDocument: Any,
) : DocumentContext {
    @Suppress("UNCHECKED_CAST")
    override fun <T> read(path: String): T = PathCompiler.compile(path).evaluate(jsonDocument) as T
}
