package com.jayway.jsonpath.internal.function.latebinding
import com.jayway.jsonpath.internal.function.Parameter
import com.jayway.jsonpath.spi.json.JsonProvider

open class JsonLateBindingValue(
    private val jsonProvider: JsonProvider,
    private val jsonParameter: Parameter,
) : ILateBindingValue {
    override fun get(): Any? = jsonProvider.parse(checkNotNull(jsonParameter.getJson()) { "JSON parameter must contain JSON text" })
}
