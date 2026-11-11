package com.jayway.jsonpath.spi.mapper

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.TypeRef

open class TapestryMappingProvider : MappingProvider {
    override fun <T> map(
        source: Any?,
        targetType: Class<T>,
        configuration: Configuration,
    ): T? {
        if (source == null) {
            return null
        }
        if (targetType.isAssignableFrom(source.javaClass)) {
            @Suppress("UNCHECKED_CAST")
            return source as T
        }
        try {
            if (targetType.isAssignableFrom(ArrayList::class.java) &&
                configuration.jsonProvider().isArray(source)
            ) {
                val length = configuration.jsonProvider().length(source)
                val list = ArrayList<Any?>(length)
                for (element in configuration.jsonProvider().toIterable(source)) {
                    list.add(element)
                }
                @Suppress("UNCHECKED_CAST")
                return list as T
            }
        } catch (_: Exception) {
        }
        throw MappingException(
            "Cannot convert a ${source.javaClass.name} to a $targetType " +
                "use Tapestry's TypeCoercer instead.",
        )
    }

    override fun <T> map(
        source: Any?,
        targetType: TypeRef<T>,
        configuration: Configuration,
    ): T? =
        throw UnsupportedOperationException(
            "Tapestry JSON provider does not support TypeRef! Use a Jackson or Gson based provider",
        )
}
