package com.jayway.jsonpath.internal

import com.jayway.jsonpath.DocumentContext

internal class JsonContext(
    private val jsonDocument: String,
) : DocumentContext {
    override fun <T> read(path: String): T {
        error(
            "Path evaluation for '$path' has not been implemented " +
                "(document length: ${jsonDocument.length})",
        )
    }
}
