package com.jayway.jsonpath.spi.mapper

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPathException
import com.jayway.jsonpath.TypeRef
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Callable

open class GsonMappingProvider : MappingProvider {
    private val factory: Callable<Gson>

    constructor(gson: Gson) {
        factory = Callable { gson }
    }

    constructor(factory: Callable<Gson>) {
        this.factory = factory
    }

    constructor() {
        try {
            Class.forName("com.google.gson.Gson")
            factory = Callable { Gson() }
        } catch (exception: ClassNotFoundException) {
            logger.error("Gson not found on class path. No converters configured.")
            throw JsonPathException("Gson not found on path", exception)
        }
    }

    override fun <T> map(
        source: Any?,
        targetType: Class<T>,
        configuration: Configuration,
    ): T? {
        if (source == null) {
            return null
        }
        try {
            return checkNotNull(factory.call()) { "Gson factory returned null" }.getAdapter(targetType).fromJsonTree(source as JsonElement)
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
        try {
            @Suppress("UNCHECKED_CAST")
            return checkNotNull(factory.call()) { "Gson factory returned null" }
                .getAdapter(TypeToken.get(targetType.getType()))
                .fromJsonTree(source as JsonElement) as T?
        } catch (exception: Exception) {
            throw MappingException(exception)
        }
    }

    private companion object {
        private val logger: Logger = LoggerFactory.getLogger(GsonMappingProvider::class.java)
    }
}
