package com.jayway.jsonpath.spi.mapper

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.TypeRef
import tools.jackson.databind.JavaType
import tools.jackson.databind.ObjectMapper

open class Jackson3MappingProvider(
    private val objectMapper: ObjectMapper,
) : MappingProvider {
    constructor() : this(ObjectMapper())

    override fun <T> map(
        source: Any?,
        targetType: Class<T>,
        configuration: Configuration,
    ): T? {
        if (source == null) {
            return null
        }
        try {
            return objectMapper.convertValue(source, targetType)
        } catch (exception: Exception) {
            throw MappingException(exception)
        }
    }

    override fun <T> map(
        source: Any?,
        targetType: TypeRef<T>,
        configuration: Configuration,
    ): T? {
        if (source == null) {
            return null
        }
        val type: JavaType = objectMapper.typeFactory.constructType(targetType.getType())
        try {
            return objectMapper.convertValue(source, type)
        } catch (exception: Exception) {
            throw MappingException(exception)
        }
    }
}
