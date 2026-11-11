package com.jayway.jsonpath.spi.json

import com.jayway.jsonpath.JsonPathException

abstract class AbstractJsonProvider : JsonProvider {
    override fun isArray(obj: Any?): Boolean = obj is List<*>

    override fun getArrayIndex(
        obj: Any?,
        idx: Int,
    ): Any? = (obj as List<*>)[idx]

    @Deprecated("Use getArrayIndex(obj, idx) instead", ReplaceWith("getArrayIndex(obj, idx)"))
    final override fun getArrayIndex(
        obj: Any?,
        idx: Int,
        unwrap: Boolean,
    ): Any? = getArrayIndex(obj, idx)

    @Suppress("UNCHECKED_CAST")
    override fun setArrayIndex(
        array: Any?,
        idx: Int,
        newValue: Any?,
    ) {
        if (!isArray(array)) {
            throw UnsupportedOperationException()
        } else {
            val list = array as MutableList<Any?>
            if (idx == list.size) {
                list.add(newValue)
            } else {
                list[idx] = newValue
            }
        }
    }

    override fun getMapValue(
        obj: Any?,
        key: String?,
    ): Any? {
        val map = obj as Map<*, *>
        return if (!map.containsKey(key)) {
            JsonProvider.UNDEFINED
        } else {
            map[key]
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun setProperty(
        obj: Any?,
        key: Any?,
        value: Any?,
    ) {
        if (isMap(obj)) {
            (obj as MutableMap<Any?, Any?>)[requireNotNull(key) { "A property key or array index is required" }.toString()] = value
        } else {
            throw JsonPathException(obj?.javaClass?.name ?: "null")
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun removeProperty(
        obj: Any?,
        key: Any?,
    ) {
        if (isMap(obj)) {
            (obj as MutableMap<Any?, Any?>).remove(requireNotNull(key) { "A property key or array index is required" }.toString())
        } else {
            val list = obj as MutableList<Any?>
            val index =
                if (key is Int) {
                    key
                } else {
                    Integer.parseInt(requireNotNull(key) { "A property key or array index is required" }.toString())
                }
            list.removeAt(index)
        }
    }

    override fun isMap(obj: Any?): Boolean = obj is Map<*, *>

    @Suppress("UNCHECKED_CAST")
    override fun getPropertyKeys(obj: Any?): Collection<String> {
        if (isArray(obj)) {
            throw UnsupportedOperationException()
        }
        return (obj as Map<*, *>).keys as Collection<String>
    }

    override fun length(obj: Any?): Int {
        if (isArray(obj)) {
            return (obj as List<*>).size
        } else if (isMap(obj)) {
            return getPropertyKeys(obj).size
        } else if (obj is String) {
            return obj.length
        }
        throw JsonPathException(
            "length operation cannot be applied to ${obj?.javaClass?.name ?: "null"}",
        )
    }

    override fun toIterable(obj: Any?): Iterable<*> {
        if (isArray(obj)) {
            return obj as Iterable<*>
        }
        throw JsonPathException(obj?.javaClass?.name ?: "null")
    }

    override fun unwrap(obj: Any?): Any? = obj
}
