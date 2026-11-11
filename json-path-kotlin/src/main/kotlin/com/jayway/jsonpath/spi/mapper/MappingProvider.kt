package com.jayway.jsonpath.spi.mapper

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.TypeRef

interface MappingProvider {
    fun <T> map(
        source: Any?,
        targetType: Class<T>,
        configuration: Configuration,
    ): T?

    fun <T> map(
        source: Any?,
        targetType: TypeRef<T>,
        configuration: Configuration,
    ): T?
}
