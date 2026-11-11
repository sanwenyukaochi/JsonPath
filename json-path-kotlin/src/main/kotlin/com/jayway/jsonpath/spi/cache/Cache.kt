package com.jayway.jsonpath.spi.cache
import com.jayway.jsonpath.JsonPath

interface Cache {
    fun get(key: String): JsonPath?

    fun put(
        key: String,
        value: JsonPath,
    )
}
