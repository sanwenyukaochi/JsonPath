package com.jayway.jsonpath.spi.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.TypeRef

open class JacksonMappingProvider(
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
        val type = objectMapper.typeFactory.constructType(targetType.getType())
        try {
            return objectMapper.convertValue(source, type)
        } catch (exception: Exception) {
            throw MappingException(exception)
        }
    }
}
