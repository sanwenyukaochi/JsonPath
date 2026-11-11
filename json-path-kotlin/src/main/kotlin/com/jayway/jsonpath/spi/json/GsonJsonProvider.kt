package com.jayway.jsonpath.spi.json

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import com.jayway.jsonpath.InvalidJsonException
import com.jayway.jsonpath.JsonPathException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.math.BigDecimal
import java.math.BigInteger

@Suppress("DEPRECATION")
open class GsonJsonProvider : AbstractJsonProvider {
    private val gson: Gson

    constructor() : this(Gson())

    constructor(gson: Gson) : super() {
        this.gson = gson
    }

    override fun unwrap(obj: Any?): Any? {
        if (obj == null) {
            return null
        }
        if (obj !is JsonElement) {
            return obj
        }

        if (obj.isJsonNull) {
            return null
        } else if (obj.isJsonPrimitive) {
            val primitive = obj.asJsonPrimitive
            if (primitive.isString) {
                return primitive.asString
            } else if (primitive.isBoolean) {
                return primitive.asBoolean
            } else if (primitive.isNumber) {
                return unwrapNumber(primitive.asNumber)
            }
        }
        return obj
    }

    @Throws(InvalidJsonException::class)
    override fun parse(json: String): Any? = PARSER.parse(json)

    @Throws(InvalidJsonException::class)
    override fun parse(
        jsonStream: InputStream,
        charset: String,
    ): Any? =
        try {
            PARSER.parse(InputStreamReader(jsonStream, charset))
        } catch (exception: UnsupportedEncodingException) {
            throw JsonPathException(exception)
        }

    override fun toJson(obj: Any?): String = gson.toJson(obj)

    override fun createArray(): Any = JsonArray()

    override fun createMap(): Any = JsonObject()

    override fun isArray(obj: Any?): Boolean = obj is JsonArray || obj is List<*>

    override fun getArrayIndex(
        obj: Any?,
        idx: Int,
    ): Any? = toJsonArray(obj)[idx]

    override fun setArrayIndex(
        array: Any?,
        idx: Int,
        newValue: Any?,
    ) {
        if (!isArray(array)) {
            throw UnsupportedOperationException()
        } else {
            val jsonArray = toJsonArray(array)
            if (idx == jsonArray.size()) {
                jsonArray.add(createJsonElement(newValue))
            } else {
                jsonArray.set(idx, createJsonElement(newValue))
            }
        }
    }

    override fun getMapValue(
        obj: Any?,
        key: String?,
    ): Any? {
        val jsonObject = toJsonObject(obj)
        val value = jsonObject.get(key)
        return if (!jsonObject.has(key)) {
            JsonProvider.UNDEFINED
        } else {
            unwrap(value)
        }
    }

    override fun setProperty(
        obj: Any?,
        key: Any?,
        value: Any?,
    ) {
        if (isMap(obj)) {
            toJsonObject(obj).add(requireNotNull(key) { "A property key or array index is required" }.toString(), createJsonElement(value))
        } else {
            val array = toJsonArray(obj)
            val index =
                if (key != null) {
                    if (key is Int) key else Integer.parseInt(key.toString())
                } else {
                    array.size()
                }
            if (index == array.size()) {
                array.add(createJsonElement(value))
            } else {
                array.set(index, createJsonElement(value))
            }
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

    override fun isMap(obj: Any?): Boolean = obj is JsonObject

    override fun getPropertyKeys(obj: Any?): Collection<String> {
        val keys = ArrayList<String>()
        for (entry in toJsonObject(obj).entrySet()) {
            keys.add(entry.key)
        }
        return keys
    }

    override fun length(obj: Any?): Int {
        if (isArray(obj)) {
            return toJsonArray(obj).size()
        } else if (isMap(obj)) {
            return toJsonObject(obj).entrySet().size
        } else if (obj is JsonElement) {
            val element = toJsonElement(obj)
            if (element.isJsonPrimitive) {
                return element.toString().length
            }
        }
        throw JsonPathException(
            "length operation can not applied to ${obj?.javaClass?.name ?: "null"}",
        )
    }

    override fun toIterable(obj: Any?): Iterable<*> {
        val array = toJsonArray(obj)
        val values = ArrayList<Any?>(array.size())
        for (value in array) {
            values.add(unwrap(value))
        }
        return values
    }

    private fun createJsonElement(value: Any?): JsonElement = gson.toJsonTree(value)

    private fun toJsonArray(value: Any?): JsonArray = requireNotNull(value as? JsonArray) { "Expected a JsonArray" }

    private fun toJsonObject(value: Any?): JsonObject = requireNotNull(value as? JsonObject) { "Expected a JsonObject" }

    private fun toJsonElement(value: Any?): JsonElement = requireNotNull(value as? JsonElement) { "Expected a JsonElement" }

    private companion object {
        private val PARSER = JsonParser()

        private fun isPrimitiveNumber(number: Number): Boolean =
            number is Int ||
                number is Float ||
                number is Double ||
                number is Long ||
                number is BigDecimal ||
                number is BigInteger

        private fun unwrapNumber(number: Number): Number {
            if (isPrimitiveNumber(number)) {
                return number
            }

            val bigDecimal = BigDecimal(number.toString())
            if (bigDecimal.scale() <= 0) {
                return if (bigDecimal.abs().compareTo(BigDecimal(Int.MAX_VALUE)) <= 0) {
                    bigDecimal.toInt()
                } else if (bigDecimal.abs().compareTo(BigDecimal(Long.MAX_VALUE)) <= 0) {
                    bigDecimal.toLong()
                } else {
                    bigDecimal
                }
            }

            val doubleValue = bigDecimal.toDouble()
            return if (BigDecimal.valueOf(doubleValue).compareTo(bigDecimal) != 0) {
                bigDecimal
            } else {
                doubleValue
            }
        }
    }
}
