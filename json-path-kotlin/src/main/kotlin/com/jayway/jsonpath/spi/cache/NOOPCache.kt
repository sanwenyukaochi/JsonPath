package com.jayway.jsonpath.spi.cache
import com.jayway.jsonpath.JsonPath

open class NOOPCache : Cache {
    override fun get(key: String): JsonPath? = null

    override fun put(
        key: String,
        value: JsonPath,
    ) {}
}
