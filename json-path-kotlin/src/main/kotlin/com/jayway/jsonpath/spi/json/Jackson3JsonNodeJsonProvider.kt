package com.jayway.jsonpath.spi.json

import com.jayway.jsonpath.InvalidJsonException
import com.jayway.jsonpath.JsonPathException
import tools.jackson.core.JacksonException
import tools.jackson.databind.JsonNode
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.node.ArrayNode
import tools.jackson.databind.node.JsonNodeFactory
import tools.jackson.databind.node.ObjectNode
import tools.jackson.databind.node.StringNode
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.charset.StandardCharsets

open class Jackson3JsonNodeJsonProvider(
    @JvmField protected var objectMapper: ObjectMapper,
) : AbstractJsonProvider() {
    constructor() : this(defaultObjectMapper)

    open fun getObjectMapper(): ObjectMapper = objectMapper

    @Throws(InvalidJsonException::class)
    override fun parse(json: String): Any? =
        try {
            objectMapper.readTree(json)
        } catch (exception: JacksonException) {
            throw InvalidJsonException(exception, json)
        }

    @Throws(InvalidJsonException::class)
    override fun parse(json: ByteArray): Any? =
        try {
            objectMapper.readTree(json)
        } catch (exception: JacksonException) {
            throw InvalidJsonException(exception, String(json, StandardCharsets.UTF_8))
        }

    @Throws(InvalidJsonException::class)
    override fun parse(
        jsonStream: InputStream,
        charset: String,
    ): Any? =
        try {
            objectMapper.readTree(InputStreamReader(jsonStream, charset))
        } catch (exception: IOException) {
            throw InvalidJsonException(exception)
        }

    override fun toJson(obj: Any?): String {
        if (obj !is JsonNode) {
            throw JsonPathException("Not a JSON Node")
        }
        return obj.toString()
    }

    override fun createArray(): Any = JsonNodeFactory.instance.arrayNode()

    override fun createMap(): Any = JsonNodeFactory.instance.objectNode()

    override fun unwrap(obj: Any?): Any? {
        if (obj == null) {
            return null
        }
        if (obj !is JsonNode) {
            return obj
        }

        if (obj.isValueNode) {
            if (obj.isString) {
                return obj.asString()
            } else if (obj.isBoolean) {
                return obj.asBoolean()
            } else if (obj.isInt) {
                return obj.asInt()
            } else if (obj.isLong) {
                return obj.asLong()
            } else if (obj.isBigInteger) {
                return obj.bigIntegerValue()
            } else if (obj.isDouble) {
                return obj.doubleValue()
            } else if (obj.isFloat) {
                return obj.floatValue()
            } else if (obj.isBigDecimal) {
                return obj.decimalValue()
            } else if (obj.isNull) {
                return null
            }
        }
        return obj
    }

    override fun isArray(obj: Any?): Boolean = obj is ArrayNode || obj is List<*>

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
            val arrayNode = toJsonArray(array)
            if (idx == arrayNode.size()) {
                arrayNode.add(createJsonElement(newValue))
            } else {
                arrayNode.set(idx, createJsonElement(newValue))
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
            value
        }
    }

    override fun setProperty(
        obj: Any?,
        key: Any?,
        value: Any?,
    ) {
        if (isMap(obj)) {
            setValueInObjectNode(obj as ObjectNode, key, value)
        } else {
            val array = obj as ArrayNode
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

    override fun isMap(obj: Any?): Boolean = obj is ObjectNode

    override fun getPropertyKeys(obj: Any?): Collection<String> = toJsonObject(obj).propertyNames()

    override fun length(obj: Any?): Int {
        if (isArray(obj)) {
            return toJsonArray(obj).size()
        } else if (isMap(obj)) {
            return toJsonObject(obj).size()
        } else if (obj is StringNode) {
            return obj.size()
        }
        throw JsonPathException(
            "length operation can not applied to ${obj?.javaClass?.name ?: "null"}",
        )
    }

    override fun toIterable(obj: Any?): Iterable<*> {
        val array = toJsonArray(obj)
        val iterator = array.iterator()
        return object : Iterable<Any?> {
            override fun iterator(): Iterator<Any?> =
                object : Iterator<Any?> {
                    override fun hasNext(): Boolean = iterator.hasNext()

                    override fun next(): Any? = unwrap(iterator.next())
                }
        }
    }

    private fun createJsonElement(value: Any?): JsonNode? {
        if (value != null) {
            if (value is JsonNode) {
                return value
            }
            return objectMapper.valueToTree(value)
        }
        return null
    }

    private fun toJsonArray(value: Any?): ArrayNode = requireNotNull(value as? ArrayNode) { "Expected a ArrayNode" }

    private fun toJsonObject(value: Any?): ObjectNode = requireNotNull(value as? ObjectNode) { "Expected a ObjectNode" }

    private fun setValueInObjectNode(
        objectNode: ObjectNode,
        key: Any?,
        value: Any?,
    ) {
        val keyString = requireNotNull(key) { "A property key or array index is required" }.toString()
        when (value) {
            is JsonNode -> objectNode.set(keyString, value)
            is String -> objectNode.put(keyString, value)
            is Int -> objectNode.put(keyString, value)
            is Long -> objectNode.put(keyString, value)
            is Short -> objectNode.put(keyString, value)
            is BigInteger -> objectNode.put(keyString, value)
            is Double -> objectNode.put(keyString, value)
            is Float -> objectNode.put(keyString, value)
            is BigDecimal -> objectNode.put(keyString, value)
            is Boolean -> objectNode.put(keyString, value)
            is ByteArray -> objectNode.put(keyString, value)
            null -> objectNode.set(keyString, null)
            else -> objectNode.set(keyString, createJsonElement(value))
        }
    }

    private companion object {
        private val defaultObjectMapper = ObjectMapper()
    }
}
