package com.jayway.jsonpath.spi.json

import com.jayway.jsonpath.InvalidJsonException
import com.jayway.jsonpath.JsonPathException
import jakarta.json.JsonArray
import jakarta.json.JsonArrayBuilder
import jakarta.json.JsonBuilderFactory
import jakarta.json.JsonNumber
import jakarta.json.JsonObject
import jakarta.json.JsonObjectBuilder
import jakarta.json.JsonReader
import jakarta.json.JsonString
import jakarta.json.JsonStructure
import jakarta.json.JsonValue
import jakarta.json.spi.JsonProvider
import jakarta.json.stream.JsonParsingException
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.io.StringReader
import java.io.UnsupportedEncodingException
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.util.ConcurrentModificationException
import java.util.LinkedList
import java.util.function.Function

open class JakartaJsonProvider(
    private val mutableJson: Boolean,
) : AbstractJsonProvider() {
    constructor() : this(false)

    @Throws(InvalidJsonException::class)
    override fun parse(json: String): Any = parse(StringReader(json))

    @Throws(InvalidJsonException::class)
    override fun parse(json: ByteArray): Any = parse(InputStreamReader(ByteArrayInputStream(json), StandardCharsets.UTF_8))

    @Throws(InvalidJsonException::class)
    override fun parse(
        jsonStream: InputStream,
        charset: String,
    ): Any =
        try {
            parse(InputStreamReader(jsonStream, charset))
        } catch (exception: UnsupportedEncodingException) {
            throw JsonPathException(exception)
        }

    private fun parse(jsonInput: Reader): Any =
        try {
            defaultJsonProvider.createReader(jsonInput).use { jsonReader: JsonReader ->
                val jsonStructure = jsonReader.read()
                if (mutableJson) proxyAll(jsonStructure) else jsonStructure
            }
        } catch (exception: JsonParsingException) {
            throw InvalidJsonException(exception)
        }

    override fun toJson(obj: Any?): String {
        var value = obj
        if (value is JsonObjectBuilder) {
            value = value.build()
        } else if (value is JsonArrayBuilder) {
            value = value.build()
        } else if (value is List<*>) {
            value = jsonBuilderFactory.createArrayBuilder(value).build()
        }
        return value.toString()
    }

    override fun createArray(): Any =
        if (mutableJson) {
            JsonArrayProxy(jsonBuilderFactory.createArrayBuilder().build())
        } else {
            LinkedList<Any?>()
        }

    override fun createMap(): Any =
        if (mutableJson) {
            JsonObjectProxy(jsonBuilderFactory.createObjectBuilder().build())
        } else {
            jsonBuilderFactory.createObjectBuilder()
        }

    override fun isArray(obj: Any?): Boolean = obj is JsonArray || obj is JsonArrayBuilder || obj is List<*>

    override fun getArrayIndex(
        obj: Any?,
        idx: Int,
    ): Any? {
        var value = obj
        if (value is JsonArrayBuilder) {
            value = value.build()
        }
        return when (value) {
            is JsonArray -> value[idx]
            is List<*> -> super.getArrayIndex(value, idx)
            else -> throw UnsupportedOperationException()
        }
    }

    override fun setArrayIndex(
        array: Any?,
        idx: Int,
        newValue: Any?,
    ) {
        when (array) {
            is JsonArrayBuilder -> {
                if (idx == array.build().size) {
                    array.add(wrap(newValue))
                } else {
                    array.set(idx, wrap(newValue))
                }
            }

            is JsonArray -> {
                if (mutableJson && array is JsonArrayProxy) {
                    array[idx] = wrap(newValue)
                } else {
                    throw UnsupportedOperationException("JsonArray is immutable in JSON-P")
                }
            }

            else -> {
                super.setArrayIndex(array, idx, wrap(newValue))
            }
        }
    }

    override fun getMapValue(
        obj: Any?,
        key: String?,
    ): Any? {
        var value = obj
        if (value is JsonObjectBuilder) {
            value = value.build()
        }
        if (value is JsonObject) {
            val jsonValue = value[key]
            return if (jsonValue == null) {
                com.jayway.jsonpath.spi.json.JsonProvider.UNDEFINED
            } else {
                unwrap(jsonValue)
            }
        }
        throw UnsupportedOperationException()
    }

    override fun setProperty(
        obj: Any?,
        key: Any?,
        value: Any?,
    ) {
        when {
            obj is JsonObjectBuilder -> {
                obj.add(requireNotNull(key) { "An object property key is required" }.toString(), wrap(value))
            }

            mutableJson && obj is JsonObjectProxy -> {
                obj[requireNotNull(key) { "An object property key is required" }.toString()] = wrap(value)
            }

            obj is JsonObject -> {
                throw UnsupportedOperationException("JsonObject is immutable in JSON-P")
            }

            obj is JsonArrayBuilder -> {
                if (key == null) {
                    obj.add(wrap(value))
                } else {
                    obj.set(toArrayIndex(key), wrap(value))
                }
            }

            mutableJson && obj is JsonArrayProxy -> {
                if (key == null) {
                    obj.add(wrap(value))
                } else {
                    obj[toArrayIndex(key)] = wrap(value)
                }
            }

            obj is JsonArray -> {
                throw UnsupportedOperationException("JsonArray is immutable in JSON-P")
            }

            obj is MutableList<*> -> {
                @Suppress("UNCHECKED_CAST")
                val array = obj as MutableList<JsonValue>
                if (key == null) {
                    array.add(wrap(value))
                } else {
                    array.add(toArrayIndex(key), wrap(value))
                }
            }

            else -> {
                throw UnsupportedOperationException()
            }
        }
    }

    override fun removeProperty(
        obj: Any?,
        key: Any?,
    ) {
        when {
            obj is JsonObjectBuilder -> {
                obj.remove(requireNotNull(key) { "An object property key is required" }.toString())
            }

            obj is JsonObject -> {
                if (mutableJson && obj is JsonObjectProxy) {
                    obj.removeValue(key)
                } else {
                    throw UnsupportedOperationException("JsonObject is immutable in JSON-P")
                }
            }

            isArray(obj) -> {
                val index = toArrayIndex(key)
                if (obj is JsonArrayBuilder) {
                    obj.remove(index)
                } else if (obj is MutableList<*>) {
                    obj.removeAt(index)
                }
            }

            else -> {
                throw UnsupportedOperationException()
            }
        }
    }

    override fun isMap(obj: Any?): Boolean = obj is JsonObject || obj is JsonObjectBuilder

    override fun getPropertyKeys(obj: Any?): Collection<String> {
        val keys =
            when (obj) {
                is JsonObjectBuilder -> obj.build().keys
                is JsonObject -> obj.keys
                else -> throw UnsupportedOperationException("Json object is expected")
            }
        return ArrayList(keys)
    }

    override fun length(obj: Any?): Int {
        if (isArray(obj)) {
            return if (obj is JsonArrayBuilder) obj.build().size else (obj as List<*>).size
        } else if (isMap(obj)) {
            val value = if (obj is JsonObjectBuilder) obj.build() else obj
            return (value as JsonObject).size
        } else if (obj is CharSequence) {
            return obj.length
        }
        val className = obj?.javaClass?.name
        throw JsonPathException("length operation can not applied to $className")
    }

    override fun toIterable(obj: Any?): Iterable<*> {
        val values: MutableList<Any?>
        var value = obj
        if (isArray(value)) {
            if (value is JsonArrayBuilder) {
                value = value.build()
            }
            values = ArrayList((value as List<*>).size)
            for (element in value) {
                values.add(unwrap(element))
            }
        } else if (isMap(value)) {
            if (value is JsonObjectBuilder) {
                value = value.build()
            }
            values = ArrayList((value as JsonObject).size)
            for (element in value.values) {
                values.add(unwrap(element))
            }
        } else {
            throw UnsupportedOperationException("an array or object instance is expected")
        }
        return values
    }

    override fun unwrap(obj: Any?): Any? {
        if (obj == null) {
            return null
        }
        if (obj !is JsonValue) {
            return obj
        }
        return when (obj.valueType) {
            JsonValue.ValueType.ARRAY -> {
                if (mutableJson && obj is JsonArrayProxy) {
                    obj
                } else {
                    (obj as JsonArray).getValuesAs(Function<JsonValue, Any?> { unwrap(it) })
                }
            }

            JsonValue.ValueType.STRING -> {
                (obj as JsonString).string
            }

            JsonValue.ValueType.NUMBER -> {
                val number = obj as JsonNumber
                if (number.isIntegral) {
                    try {
                        number.intValueExact()
                    } catch (_: ArithmeticException) {
                        number.longValueExact()
                    }
                } else {
                    number.doubleValue()
                }
            }

            JsonValue.ValueType.TRUE -> {
                true
            }

            JsonValue.ValueType.FALSE -> {
                false
            }

            JsonValue.ValueType.NULL -> {
                null
            }

            else -> {
                obj
            }
        }
    }

    private fun toArrayIndex(index: Any?): Int =
        try {
            when (index) {
                is Int -> index
                is Long -> index.toInt()
                null -> throw IllegalArgumentException("Invalid array index")
                else -> Integer.valueOf(index.toString())
            }
        } catch (exception: NumberFormatException) {
            throw JsonPathException(exception)
        }

    private fun wrap(obj: Any?): JsonValue =
        when {
            obj == null -> {
                JsonValue.NULL
            }

            obj is JsonArray -> {
                if (!mutableJson || obj is JsonArrayProxy) obj else proxyAll(obj)
            }

            obj is JsonObject -> {
                if (!mutableJson || obj is JsonObjectProxy) obj else proxyAll(obj)
            }

            obj is JsonValue -> {
                obj
            }

            true == obj -> {
                JsonValue.TRUE
            }

            false == obj -> {
                JsonValue.FALSE
            }

            obj is CharSequence -> {
                defaultJsonProvider.createValue(obj.toString())
            }

            obj is Number -> {
                when (obj) {
                    is Int -> defaultJsonProvider.createValue(obj)
                    is Long -> defaultJsonProvider.createValue(obj)
                    is Float, is Double -> defaultJsonProvider.createValue(obj.toDouble())
                    is BigInteger -> defaultJsonProvider.createValue(obj)
                    is BigDecimal -> defaultJsonProvider.createValue(obj)
                    else -> defaultJsonProvider.createValue(BigDecimal.valueOf(obj.toDouble()))
                }
            }

            obj is Collection<*> -> {
                val result = jsonBuilderFactory.createArrayBuilder(obj).build()
                if (mutableJson) proxyAll(result) else result
            }

            obj is Map<*, *> -> {
                @Suppress("UNCHECKED_CAST")
                val map = obj as Map<String, Any?>
                val result = jsonBuilderFactory.createObjectBuilder(map).build()
                if (mutableJson) proxyAll(result) else result
            }

            obj is JsonArrayBuilder -> {
                val result = obj.build()
                if (mutableJson) proxyAll(result) else result
            }

            obj is JsonObjectBuilder -> {
                val result = obj.build()
                if (mutableJson) proxyAll(result) else result
            }

            else -> {
                throw UnsupportedOperationException(
                    "Cannot create JSON element from ${obj.javaClass.simpleName}",
                )
            }
        }

    private fun proxyAll(jsonStructure: JsonStructure): JsonStructure {
        if (jsonStructure is JsonArrayProxy) {
            return jsonStructure
        } else if (jsonStructure is JsonArray) {
            val array = ArrayList<Any?>()
            for (element in jsonStructure) {
                var value = element
                if (value is JsonStructure) {
                    value = proxyAll(value)
                }
                array.add(value)
            }
            return JsonArrayProxy(jsonBuilderFactory.createArrayBuilder(array).build())
        } else if (jsonStructure is JsonObjectProxy) {
            return jsonStructure
        } else if (jsonStructure is JsonObject) {
            val map = LinkedHashMap<String, Any?>()
            for ((key, originalValue) in jsonStructure) {
                var value = originalValue
                if (value is JsonStructure) {
                    value = proxyAll(value)
                }
                map[key] = value
            }
            return JsonObjectProxy(jsonBuilderFactory.createObjectBuilder(map).build())
        }
        throw IllegalArgumentException()
    }

    private class JsonArrayProxy(
        private var array: JsonArray,
    ) : JsonArray {
        override fun getJsonObject(index: Int): JsonObject? = array.getJsonObject(index)

        override fun getJsonArray(index: Int): JsonArray? = array.getJsonArray(index)

        override fun getJsonNumber(index: Int): JsonNumber? = array.getJsonNumber(index)

        override fun getJsonString(index: Int): JsonString? = array.getJsonString(index)

        override fun <T : JsonValue> getValuesAs(clazz: Class<T>): List<T> = array.getValuesAs(clazz)

        override fun getString(index: Int): String = array.getString(index)

        override fun getString(
            index: Int,
            defaultValue: String?,
        ): String? = array.getString(index, defaultValue)

        override fun getInt(index: Int): Int = array.getInt(index)

        override fun getInt(
            index: Int,
            defaultValue: Int,
        ): Int = array.getInt(index, defaultValue)

        override fun getBoolean(index: Int): Boolean = array.getBoolean(index)

        override fun getBoolean(
            index: Int,
            defaultValue: Boolean,
        ): Boolean = array.getBoolean(index, defaultValue)

        override fun isNull(index: Int): Boolean = array.isNull(index)

        override fun getValueType(): JsonValue.ValueType = array.valueType

        override val size: Int
            get() = array.size

        override fun isEmpty(): Boolean = array.isEmpty()

        override fun contains(element: JsonValue): Boolean = array.contains(element)

        override fun iterator(): MutableIterator<JsonValue> {
            val reference = array
            val iterator = array.iterator()
            return object : MutableIterator<JsonValue> {
                override fun hasNext(): Boolean {
                    if (reference === array) {
                        return iterator.hasNext()
                    }
                    throw ConcurrentModificationException()
                }

                override fun next(): JsonValue {
                    if (reference === array) {
                        return iterator.next()
                    }
                    throw ConcurrentModificationException()
                }

                override fun remove(): Unit = throw UnsupportedOperationException()
            }
        }

        override fun add(element: JsonValue): Boolean {
            array = jsonBuilderFactory.createArrayBuilder(array).add(element).build()
            return true
        }

        override fun remove(element: JsonValue): Boolean {
            val index = array.indexOf(element)
            if (index != -1) {
                array = jsonBuilderFactory.createArrayBuilder(array).remove(index).build()
                return true
            }
            return false
        }

        override fun containsAll(elements: Collection<JsonValue>): Boolean = array.containsAll(elements)

        override fun addAll(elements: Collection<JsonValue>): Boolean {
            if (!elements.isEmpty()) {
                val builder = jsonBuilderFactory.createArrayBuilder(array)
                for (value in elements) {
                    builder.add(value)
                }
                array = builder.build()
                return true
            }
            return false
        }

        override fun addAll(
            index: Int,
            elements: Collection<JsonValue>,
        ): Boolean {
            if (elements.isEmpty()) {
                return false
            }
            if (index < 0 || index >= array.size) {
                throw IndexOutOfBoundsException()
            }
            val builder = jsonBuilderFactory.createArrayBuilder(array)
            for (arrayIndex in array.indices) {
                if (index == arrayIndex) {
                    for (value in elements) {
                        builder.add(value)
                    }
                }
                builder.add(array[arrayIndex])
            }
            array = builder.build()
            return true
        }

        override fun removeAll(elements: Collection<JsonValue>): Boolean {
            if (elements.isEmpty()) {
                return false
            }
            var builder: JsonArrayBuilder? = null
            var builderIndex = 0
            for (arrayIndex in array.indices) {
                if (elements.contains(array[arrayIndex])) {
                    if (builder == null) {
                        builder = jsonBuilderFactory.createArrayBuilder(array)
                    }
                    builder.remove(builderIndex--)
                }
                builderIndex++
            }
            if (builder != null) {
                array = builder.build()
                return true
            }
            return false
        }

        override fun retainAll(elements: Collection<JsonValue>): Boolean {
            if (elements.isEmpty()) {
                array = jsonBuilderFactory.createArrayBuilder().build()
                return true
            }
            var builder: JsonArrayBuilder? = null
            var builderIndex = 0
            for (arrayIndex in array.indices) {
                if (!elements.contains(array[arrayIndex])) {
                    if (builder == null) {
                        builder = jsonBuilderFactory.createArrayBuilder(array)
                    }
                    builder.remove(builderIndex--)
                }
                builderIndex++
            }
            if (builder != null) {
                array = builder.build()
                return true
            }
            return false
        }

        override fun clear() {
            array = jsonBuilderFactory.createArrayBuilder().build()
        }

        override fun get(index: Int): JsonValue = array[index]

        override fun set(
            index: Int,
            element: JsonValue,
        ): JsonValue? {
            if (index == array.size) {
                array = jsonBuilderFactory.createArrayBuilder(array).add(index, element).build()
                return null
            }
            val oldValue = array[index]
            array = jsonBuilderFactory.createArrayBuilder(array).set(index, element).build()
            return oldValue
        }

        override fun add(
            index: Int,
            element: JsonValue,
        ) {
            array = jsonBuilderFactory.createArrayBuilder(array).add(index, element).build()
        }

        override fun removeAt(index: Int): JsonValue {
            val oldValue = array[index]
            array = jsonBuilderFactory.createArrayBuilder(array).remove(index).build()
            return oldValue
        }

        override fun indexOf(element: JsonValue): Int = array.indexOf(element)

        override fun lastIndexOf(element: JsonValue): Int = array.lastIndexOf(element)

        override fun listIterator(): MutableListIterator<JsonValue> = listIterator(0)

        override fun listIterator(index: Int): MutableListIterator<JsonValue> {
            val reference = array
            val iterator = array.listIterator(index)
            return object : MutableListIterator<JsonValue> {
                override fun hasNext(): Boolean {
                    if (reference === array) {
                        return iterator.hasNext()
                    }
                    throw ConcurrentModificationException()
                }

                override fun next(): JsonValue {
                    if (reference === array) {
                        return iterator.next()
                    }
                    throw ConcurrentModificationException()
                }

                override fun hasPrevious(): Boolean {
                    if (reference === array) {
                        return iterator.hasPrevious()
                    }
                    throw ConcurrentModificationException()
                }

                override fun previous(): JsonValue {
                    if (reference === array) {
                        return iterator.previous()
                    }
                    throw ConcurrentModificationException()
                }

                override fun nextIndex(): Int {
                    if (reference === array) {
                        return iterator.nextIndex()
                    }
                    throw ConcurrentModificationException()
                }

                override fun previousIndex(): Int {
                    if (reference === array) {
                        return iterator.previousIndex()
                    }
                    throw ConcurrentModificationException()
                }

                override fun remove() {
                    iterator.remove()
                }

                override fun set(element: JsonValue) {
                    iterator.set(element)
                }

                override fun add(element: JsonValue) {
                    iterator.add(element)
                }
            }
        }

        override fun subList(
            fromIndex: Int,
            toIndex: Int,
        ): MutableList<JsonValue> = array.subList(fromIndex, toIndex)

        override fun hashCode(): Int = array.hashCode()

        override fun equals(other: Any?): Boolean =
            when (other) {
                null -> false
                is JsonArrayProxy -> array == other.array
                else -> array == other
            }

        override fun toString(): String = array.toString()
    }

    private class JsonObjectProxy(
        private var jsonObject: JsonObject,
    ) : JsonObject {
        override fun getJsonArray(name: String): JsonArray? = jsonObject.getJsonArray(name)

        override fun getJsonObject(name: String): JsonObject? = jsonObject.getJsonObject(name)

        override fun getJsonNumber(name: String): JsonNumber? = jsonObject.getJsonNumber(name)

        override fun getJsonString(name: String): JsonString? = jsonObject.getJsonString(name)

        override fun getString(name: String): String = jsonObject.getString(name)

        override fun getString(
            name: String,
            defaultValue: String?,
        ): String? = jsonObject.getString(name, defaultValue)

        override fun getInt(name: String): Int = jsonObject.getInt(name)

        override fun getInt(
            name: String,
            defaultValue: Int,
        ): Int = jsonObject.getInt(name, defaultValue)

        override fun getBoolean(name: String): Boolean = jsonObject.getBoolean(name)

        override fun getBoolean(
            name: String,
            defaultValue: Boolean,
        ): Boolean = jsonObject.getBoolean(name, defaultValue)

        override fun isNull(name: String): Boolean = jsonObject.isNull(name)

        override fun getValueType(): JsonValue.ValueType = jsonObject.valueType

        override val size: Int
            get() = jsonObject.size

        override fun isEmpty(): Boolean = jsonObject.isEmpty()

        override fun containsKey(key: String): Boolean = jsonObject.containsKey(key)

        override fun containsValue(value: JsonValue): Boolean = jsonObject.containsValue(value)

        override fun get(key: String): JsonValue? = jsonObject[key]

        override fun put(
            key: String,
            value: JsonValue,
        ): JsonValue? {
            val oldValue = jsonObject[key]
            jsonObject = jsonBuilderFactory.createObjectBuilder(jsonObject).add(key, value).build()
            return oldValue
        }

        override fun remove(key: String): JsonValue? = removeValue(key)

        fun removeValue(key: Any?): JsonValue? {
            @Suppress("UNCHECKED_CAST")
            val objectMap = jsonObject as Map<Any?, JsonValue>
            val oldValue = objectMap[key]
            if (oldValue != null) {
                jsonObject =
                    jsonBuilderFactory
                        .createObjectBuilder(
                            jsonObject,
                        ).remove(requireNotNull(key) { "An object property key is required" }.toString())
                        .build()
                return oldValue
            }
            return null
        }

        override fun putAll(from: Map<out String, JsonValue>) {
            if (from.isEmpty()) {
                return
            }
            val builder = jsonBuilderFactory.createObjectBuilder(jsonObject)
            for ((key, value) in from) {
                builder.add(key, value)
            }
            jsonObject = builder.build()
        }

        override fun clear() {
            jsonObject = jsonBuilderFactory.createObjectBuilder().build()
        }

        override val keys: MutableSet<String>
            get() = jsonObject.keys

        override val values: MutableCollection<JsonValue>
            get() = jsonObject.values

        override val entries: MutableSet<MutableMap.MutableEntry<String, JsonValue>>
            get() {
                val reference = jsonObject
                return object : AbstractMutableSet<MutableMap.MutableEntry<String, JsonValue>>() {
                    override fun add(element: MutableMap.MutableEntry<String, JsonValue>): Boolean = throw UnsupportedOperationException()

                    override fun iterator(): MutableIterator<MutableMap.MutableEntry<String, JsonValue>> {
                        if (reference !== jsonObject) {
                            throw ConcurrentModificationException()
                        }
                        val iterator = jsonObject.entries.iterator()
                        return object : MutableIterator<MutableMap.MutableEntry<String, JsonValue>> {
                            override fun hasNext(): Boolean {
                                if (reference === jsonObject) {
                                    return iterator.hasNext()
                                }
                                throw ConcurrentModificationException()
                            }

                            override fun next(): MutableMap.MutableEntry<String, JsonValue> {
                                if (reference === jsonObject) {
                                    return iterator.next()
                                }
                                throw ConcurrentModificationException()
                            }

                            override fun remove(): Unit = throw UnsupportedOperationException()
                        }
                    }

                    override val size: Int
                        get() {
                            if (reference === jsonObject) {
                                return jsonObject.size
                            }
                            throw ConcurrentModificationException()
                        }
                }
            }

        override fun hashCode(): Int = jsonObject.hashCode()

        override fun equals(other: Any?): Boolean =
            when (other) {
                null -> false
                is JsonObjectProxy -> jsonObject == other.jsonObject
                else -> jsonObject == other
            }

        override fun toString(): String = jsonObject.toString()
    }

    private companion object {
        private val defaultJsonProvider = JsonProvider.provider()
        private val jsonBuilderFactory: JsonBuilderFactory =
            defaultJsonProvider.createBuilderFactory(null)
    }
}
