package com.jayway.jsonpath.spi.mapper

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.TypeRef
import org.json.JSONArray
import org.json.JSONObject

open class JsonOrgMappingProvider : MappingProvider {
    override fun <T> map(
        source: Any?,
        targetType: Class<T>,
        configuration: Configuration,
    ): T? {
        if (source == null) {
            return null
        }
        if (targetType.equals(Any::class.java) ||
            targetType == List::class.java ||
            targetType == Map::class.java
        ) {
            @Suppress("UNCHECKED_CAST")
            return mapToObject(source) as T?
        }
        @Suppress("UNCHECKED_CAST")
        return source as T
    }

    override fun <T> map(
        source: Any?,
        targetType: TypeRef<T>,
        configuration: Configuration,
    ): T? =
        throw UnsupportedOperationException(
            "JsonOrg provider does not support TypeRef! Use a Jackson or Gson based provider",
        )

    private fun mapToObject(source: Any?): Any? {
        if (source is JSONArray) {
            val mapped = ArrayList<Any?>()
            for (index in 0 until source.length()) {
                mapped.add(mapToObject(source.get(index)))
            }
            return mapped
        } else if (source is JSONObject) {
            val mapped = HashMap<String, Any?>()
            for (keyObject in source.keySet()) {
                val key = keyObject.toString()
                mapped[key] = mapToObject(source.get(key))
            }
            return mapped
        } else if (source === JSONObject.NULL) {
            return null
        }
        return source
    }
}
