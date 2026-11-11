package com.jayway.jsonpath.spi.json

import com.jayway.jsonpath.InvalidJsonException
import com.jayway.jsonpath.JsonPathException
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.io.InputStream
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException

open class JsonOrgJsonProvider : AbstractJsonProvider() {
    @Throws(InvalidJsonException::class)
    override fun parse(json: String): Any? =
        try {
            JSONTokener(json).nextValue()
        } catch (exception: JSONException) {
            throw InvalidJsonException(exception)
        }

    @Throws(InvalidJsonException::class)
    override fun parse(
        jsonStream: InputStream,
        charset: String,
    ): Any? =
        try {
            JSONTokener(InputStreamReader(jsonStream, charset)).nextValue()
        } catch (exception: UnsupportedEncodingException) {
            throw JsonPathException(exception)
        } catch (exception: JSONException) {
            throw InvalidJsonException(exception)
        }

    override fun unwrap(obj: Any?): Any? {
        if (obj === JSONObject.NULL) {
            return null
        }
        return obj
    }

    override fun toJson(obj: Any?): String = obj.toString()

    override fun createArray(): Any = JSONArray()

    override fun createMap(): Any = JSONObject()

    override fun isArray(obj: Any?): Boolean = obj is JSONArray || obj is List<*>

    override fun getArrayIndex(
        obj: Any?,
        idx: Int,
    ): Any? =
        try {
            toJsonArray(obj)[idx]
        } catch (exception: JSONException) {
            throw JsonPathException(exception)
        }

    override fun setArrayIndex(
        array: Any?,
        idx: Int,
        newValue: Any?,
    ) {
        try {
            if (!isArray(array)) {
                throw UnsupportedOperationException()
            } else {
                toJsonArray(array).put(idx, createJsonElement(newValue))
            }
        } catch (exception: JSONException) {
            throw JsonPathException(exception)
        }
    }

    override fun getMapValue(
        obj: Any?,
        key: String?,
    ): Any? =
        try {
            val jsonObject = toJsonObject(obj)
            val value = jsonObject.opt(key)
            if (value == null) {
                JsonProvider.UNDEFINED
            } else {
                unwrap(value)
            }
        } catch (exception: JSONException) {
            throw JsonPathException(exception)
        }

    override fun setProperty(
        obj: Any?,
        key: Any?,
        value: Any?,
    ) {
        try {
            if (isMap(obj)) {
                toJsonObject(
                    obj,
                ).put(requireNotNull(key) { "A property key or array index is required" }.toString(), createJsonElement(value))
            } else {
                val array = toJsonArray(obj)
                val index =
                    if (key != null) {
                        if (key is Int) key else Integer.parseInt(key.toString())
                    } else {
                        array.length()
                    }
                if (index == array.length()) {
                    array.put(createJsonElement(value))
                } else {
                    array.put(index, createJsonElement(value))
                }
            }
        } catch (exception: JSONException) {
            throw JsonPathException(exception)
        }
    }

    override fun removeProperty(
        obj: Any?,
        key: Any?,
    ) {
        if (isMap(obj)) {
            toJsonObject(obj).remove(requireNotNull(key) { "A property key or array index is required" }.toString())
        } else {
            val array = toJsonArray(obj)
            val index =
                if (key is Int) {
                    key
                } else {
                    Integer.parseInt(
                        requireNotNull(key) { "A property key or array index is required" }.toString(),
                    )
                }
            array.remove(index)
        }
    }

    override fun isMap(obj: Any?): Boolean = obj is JSONObject

    override fun getPropertyKeys(obj: Any?): Collection<String> {
        val jsonObject = toJsonObject(obj)
        try {
            if (jsonObject.names() == null) {
                return ArrayList()
            }
            return jsonObject.keySet()
        } catch (exception: JSONException) {
            throw JsonPathException(exception)
        }
    }

    override fun length(obj: Any?): Int {
        if (isArray(obj)) {
            return toJsonArray(obj).length()
        } else if (isMap(obj)) {
            return toJsonObject(obj).length()
        } else if (obj is String) {
            return obj.length
        }
        throw JsonPathException(
            "length operation can not applied to ${obj?.javaClass?.name ?: "null"}",
        )
    }

    override fun toIterable(obj: Any?): Iterable<*> =
        try {
            if (isArray(obj)) {
                val array = toJsonArray(obj)
                val values = ArrayList<Any?>(array.length())
                for (index in 0 until array.length()) {
                    values.add(unwrap(array[index]))
                }
                values
            } else {
                val jsonObject = toJsonObject(obj)
                val values = ArrayList<Any?>()
                val names = jsonObject.names()
                var index = 0
                while (names != null && index < names.length()) {
                    val key = names[index] as String
                    val value = jsonObject[key]
                    values.add(unwrap(value))
                    index++
                }
                values
            }
        } catch (exception: JSONException) {
            throw JsonPathException(exception)
        }

    private fun createJsonElement(value: Any?): Any? = value

    private fun toJsonArray(value: Any?): JSONArray = requireNotNull(value as? JSONArray) { "Expected a JSONArray" }

    private fun toJsonObject(value: Any?): JSONObject = requireNotNull(value as? JSONObject) { "Expected a JSONObject" }
}
