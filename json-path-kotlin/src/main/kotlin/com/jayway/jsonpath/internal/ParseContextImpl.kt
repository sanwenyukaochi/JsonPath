package com.jayway.jsonpath.internal

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.ParseContext

internal class ParseContextImpl(
    private val configuration: Configuration = Configuration.defaultConfiguration(),
) : ParseContext {
    override fun parse(json: String?): DocumentContext {
        require(!json.isNullOrEmpty()) {
            "json string can not be null or empty"
        }

        val jsonDocument =
            requireNotNull(configuration.jsonProvider.parse(json)) {
                "json can not be null"
            }

        return JsonContext(jsonDocument)
    }
}
