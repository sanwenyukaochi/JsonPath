package com.jayway.jsonpath.spi.mapper

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.TypeRef
import jakarta.json.JsonArray
import jakarta.json.JsonArrayBuilder
import jakarta.json.JsonException
import jakarta.json.JsonNumber
import jakarta.json.JsonObject
import jakarta.json.JsonObjectBuilder
import jakarta.json.JsonString
import jakarta.json.JsonStructure
import jakarta.json.JsonValue
import jakarta.json.bind.Jsonb
import jakarta.json.bind.JsonbBuilder
import jakarta.json.bind.JsonbConfig
import jakarta.json.bind.JsonbException
import jakarta.json.stream.JsonLocation
import jakarta.json.stream.JsonParser
import java.lang.reflect.Constructor
import java.lang.reflect.GenericArrayType
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.BigInteger
import java.util.ArrayDeque
import java.util.Arrays
import java.util.Deque
import java.util.LinkedHashSet
import java.util.LinkedList
import java.util.NoSuchElementException
import java.util.Queue
import java.util.function.Function

open class JakartaMappingProvider : MappingProvider {
    private val jsonb: Jsonb
    private val jsonToClassMethod: Method?
    private val jsonToTypeMethod: Method?

    constructor() {
        jsonb = JsonbBuilder.create()
        jsonToClassMethod =
            findMethod(jsonb.javaClass, "fromJson", JsonParser::class.java, Class::class.java)
        jsonToTypeMethod =
            findMethod(jsonb.javaClass, "fromJson", JsonParser::class.java, Type::class.java)
    }

    constructor(jsonbConfiguration: JsonbConfig) {
        jsonb = JsonbBuilder.create(jsonbConfiguration)
        jsonToClassMethod =
            findMethod(jsonb.javaClass, "fromJson", JsonParser::class.java, Class::class.java)
        jsonToTypeMethod =
            findMethod(jsonb.javaClass, "fromJson", JsonParser::class.java, Type::class.java)
    }

    override fun <T> map(
        source: Any?,
        targetType: Class<T>,
        configuration: Configuration,
    ): T? {
        @Suppress("UNCHECKED_CAST")
        return mapImpl(source, targetType) as T?
    }

    override fun <T> map(
        source: Any?,
        targetType: TypeRef<T>,
        configuration: Configuration,
    ): T? {
        @Suppress("UNCHECKED_CAST")
        return mapImpl(source, targetType.getType()) as T?
    }

    private fun mapImpl(
        originalSource: Any?,
        targetType: Type,
    ): Any? {
        if (originalSource == null || originalSource === JsonValue.NULL) {
            return null
        }
        if (originalSource === JsonValue.TRUE) {
            if (Boolean::class.javaObjectType == targetType) {
                return true
            }
            val className = targetType.toString()
            throw MappingException("JSON boolean (true) cannot be mapped to $className")
        }
        if (originalSource === JsonValue.FALSE) {
            if (Boolean::class.javaObjectType == targetType) {
                return false
            }
            val className = targetType.toString()
            throw MappingException("JSON boolean (false) cannot be mapped to $className")
        } else if (originalSource is JsonString) {
            if (String::class.java == targetType) {
                return originalSource.chars
            }
            val className = targetType.toString()
            throw MappingException("JSON string cannot be mapped to $className")
        } else if (originalSource is JsonNumber) {
            return if (originalSource.isIntegral) {
                mapIntegralJsonNumber(originalSource, getRawClass(targetType))
            } else {
                mapDecimalJsonNumber(originalSource, getRawClass(targetType))
            }
        }

        var source = originalSource
        if (source is JsonArrayBuilder) {
            source = source.build()
        } else if (source is JsonObjectBuilder) {
            source = source.build()
        }
        if (source is Collection<*>) {
            val rawTargetType = getRawClass(targetType)
            val targetTypeArgument = getFirstTypeArgument(targetType)
            val result = newCollectionOfType(rawTargetType)
            for (sourceValue in source) {
                if (sourceValue is JsonObject) {
                    if (targetTypeArgument != null) {
                        result.add(mapImpl(sourceValue, targetTypeArgument))
                    } else {
                        result.add(sourceValue)
                    }
                } else {
                    result.add(unwrapJsonValue(sourceValue))
                }
            }
            return result
        } else if (source is JsonObject) {
            if (targetType is Class<*>) {
                if (jsonToClassMethod != null) {
                    try {
                        val jsonParser: JsonParser = JsonStructureToParserAdapter(source)
                        return jsonToClassMethod.invoke(jsonb, jsonParser, targetType)
                    } catch (exception: Exception) {
                        throw MappingException(exception)
                    }
                } else {
                    try {
                        val json = source.toString()
                        return jsonb.fromJson(json, targetType)
                    } catch (exception: JsonbException) {
                        throw MappingException(exception)
                    }
                }
            } else if (targetType is ParameterizedType) {
                if (jsonToTypeMethod != null) {
                    try {
                        val jsonParser: JsonParser = JsonStructureToParserAdapter(source)
                        return jsonToTypeMethod.invoke(jsonb, jsonParser, targetType)
                    } catch (exception: Exception) {
                        throw MappingException(exception)
                    }
                } else {
                    try {
                        val json = source.toString()
                        return jsonb.fromJson(json, targetType)
                    } catch (exception: JsonbException) {
                        throw MappingException(exception)
                    }
                }
            }
            throw MappingException("JSON object cannot be databind to $targetType")
        }
        return source
    }

    private fun <T> mapIntegralJsonNumber(
        jsonNumber: JsonNumber,
        targetType: Class<*>,
    ): T {
        @Suppress("UNCHECKED_CAST")
        if (targetType.isPrimitive) {
            if (Int::class.javaPrimitiveType == targetType) {
                return Integer.valueOf(jsonNumber.intValueExact()) as T
            } else if (Long::class.javaPrimitiveType == targetType) {
                return java.lang.Long.valueOf(jsonNumber.longValueExact()) as T
            }
        } else if (Int::class.javaObjectType == targetType) {
            return Integer.valueOf(jsonNumber.intValueExact()) as T
        } else if (Long::class.javaObjectType == targetType) {
            return java.lang.Long.valueOf(jsonNumber.longValueExact()) as T
        } else if (BigInteger::class.java == targetType) {
            return jsonNumber.bigIntegerValueExact() as T
        } else if (BigDecimal::class.java == targetType) {
            return jsonNumber.bigDecimalValue() as T
        }
        val className = targetType.simpleName
        throw MappingException("JSON integral number cannot be mapped to $className")
    }

    private fun <T> mapDecimalJsonNumber(
        jsonNumber: JsonNumber,
        targetType: Class<*>,
    ): T {
        @Suppress("UNCHECKED_CAST")
        if (targetType.isPrimitive) {
            if (Float::class.javaPrimitiveType == targetType) {
                return java.lang.Float.valueOf(jsonNumber.doubleValue().toFloat()) as T
            } else if (Double::class.javaPrimitiveType == targetType) {
                return java.lang.Double.valueOf(jsonNumber.doubleValue()) as T
            }
        } else if (Float::class.javaObjectType == targetType) {
            return java.lang.Float.valueOf(jsonNumber.doubleValue().toFloat()) as T
        } else if (Double::class.javaObjectType == targetType) {
            return java.lang.Double.valueOf(jsonNumber.doubleValue()) as T
        } else if (BigDecimal::class.java == targetType) {
            return jsonNumber.bigDecimalValue() as T
        }
        val className = targetType.simpleName
        throw MappingException("JSON decimal number cannot be mapped to $className")
    }

    private fun unwrapJsonValue(jsonValue: Any?): Any? {
        if (jsonValue == null) {
            return null
        }
        if (jsonValue !is JsonValue) {
            return jsonValue
        }
        return when (jsonValue.valueType) {
            JsonValue.ValueType.ARRAY -> {
                (jsonValue as JsonArray).getValuesAs(
                    Function<JsonValue, Any?> { unwrapJsonValue(it) },
                )
            }

            JsonValue.ValueType.OBJECT -> {
                throw IllegalArgumentException("Use map() method to databind a JsonObject")
            }

            JsonValue.ValueType.STRING -> {
                (jsonValue as JsonString).string
            }

            JsonValue.ValueType.NUMBER -> {
                val jsonNumber = jsonValue as JsonNumber
                if (jsonNumber.isIntegral) {
                    try {
                        jsonNumber.intValueExact()
                    } catch (_: ArithmeticException) {
                        jsonNumber.longValueExact()
                    }
                } else {
                    jsonNumber.doubleValue()
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
                jsonValue
            }
        }
    }

    @Throws(MappingException::class)
    private fun newCollectionOfType(collectionType: Class<*>): MutableCollection<Any?> {
        if (Collection::class.java.isAssignableFrom(collectionType)) {
            if (!collectionType.isInterface) {
                @Suppress("UNCHECKED_CAST")
                return newNoArgInstance(collectionType) as MutableCollection<Any?>
            } else if (List::class.java.isAssignableFrom(collectionType)) {
                return LinkedList()
            } else if (Set::class.java.isAssignableFrom(collectionType)) {
                return LinkedHashSet()
            } else if (Queue::class.java.isAssignableFrom(collectionType)) {
                return LinkedList()
            }
        }
        val className = collectionType.simpleName
        throw MappingException("JSON array cannot be mapped to $className")
    }

    @Throws(MappingException::class)
    private fun newNoArgInstance(targetType: Class<*>): Any? {
        if (targetType.isInterface) {
            return null
        }
        for (constructor: Constructor<*> in targetType.constructors) {
            if (constructor.parameterCount == 0) {
                try {
                    return constructor.newInstance()
                } catch (exception: ReflectiveOperationException) {
                    throw MappingException(exception)
                } catch (_: IllegalArgumentException) {
                }
            }
        }
        val className = targetType.simpleName
        throw MappingException("Unable to find no-arg ctr for $className")
    }

    private fun findMethod(
        initialClass: Class<*>?,
        name: String,
        vararg parameterTypes: Class<*>,
    ): Method? {
        var currentClass = initialClass
        while (currentClass != null && !currentClass.isInterface) {
            for (method in currentClass.declaredMethods) {
                val modifiers = method.modifiers
                if (Modifier.isPublic(modifiers) &&
                    !Modifier.isAbstract(modifiers) &&
                    name == method.name &&
                    Arrays.equals(parameterTypes, method.parameterTypes)
                ) {
                    return method
                }
            }
            currentClass = currentClass.superclass
        }
        return null
    }

    private fun getRawClass(targetType: Type): Class<*> {
        if (targetType is Class<*>) {
            return targetType
        } else if (targetType is ParameterizedType) {
            return targetType.rawType as Class<*>
        } else if (targetType is GenericArrayType) {
            val typeName = targetType.typeName
            throw MappingException("Cannot map JSON element to $typeName")
        }
        val typeName = targetType.typeName
        throw IllegalArgumentException("TypeRef not supported: $typeName")
    }

    private fun getFirstTypeArgument(targetType: Type): Type? {
        if (targetType is ParameterizedType) {
            val arguments = targetType.actualTypeArguments
            if (arguments.isNotEmpty()) {
                if (arguments[0] is Class<*>) {
                    return arguments[0] as Class<*>
                } else if (arguments[0] is ParameterizedType) {
                    return arguments[0] as ParameterizedType
                }
            }
        }
        return null
    }

    private class JsonStructureToParserAdapter(
        jsonStructure: JsonStructure,
    ) : JsonParser {
        private var scope: JsonStructureScope = createScope(jsonStructure)
        private var state: JsonParser.Event? = null
        private val ancestry: Deque<JsonStructureScope> = ArrayDeque()

        override fun hasNext(): Boolean =
            !(
                (state == JsonParser.Event.END_ARRAY || state == JsonParser.Event.END_OBJECT) &&
                    ancestry.isEmpty()
            )

        override fun next(): JsonParser.Event {
            if (!hasNext()) {
                throw NoSuchElementException()
            }
            if (state == null) {
                state =
                    if (scope is JsonArrayScope) {
                        JsonParser.Event.START_ARRAY
                    } else {
                        JsonParser.Event.START_OBJECT
                    }
            } else {
                if (state == JsonParser.Event.END_ARRAY || state == JsonParser.Event.END_OBJECT) {
                    scope = ancestry.pop()
                }
                if (scope is JsonArrayScope) {
                    if (scope.hasNext()) {
                        scope.next()
                        state = getState(scope.getValue())
                        if (state == JsonParser.Event.START_ARRAY ||
                            state == JsonParser.Event.START_OBJECT
                        ) {
                            ancestry.push(scope)
                            scope = createScope(scope.getValue())
                        }
                    } else {
                        state = JsonParser.Event.END_ARRAY
                    }
                } else {
                    if (state == JsonParser.Event.KEY_NAME) {
                        state = getState(scope.getValue())
                        if (state == JsonParser.Event.START_ARRAY ||
                            state == JsonParser.Event.START_OBJECT
                        ) {
                            ancestry.push(scope)
                            scope = createScope(scope.getValue())
                        }
                    } else if (scope.hasNext()) {
                        scope.next()
                        state = JsonParser.Event.KEY_NAME
                    } else {
                        state = JsonParser.Event.END_OBJECT
                    }
                }
            }
            return checkNotNull(state) { "Parser has no current event" }
        }

        override fun getString(): String =
            when (state) {
                JsonParser.Event.KEY_NAME -> {
                    (scope as JsonObjectScope).getKey()
                }

                JsonParser.Event.VALUE_STRING -> {
                    (scope.getValue() as JsonString).string
                }

                JsonParser.Event.VALUE_NUMBER -> {
                    (scope.getValue() as JsonNumber).toString()
                }

                else -> {
                    throw IllegalStateException(
                        "Parser is not in KEY_NAME, VALUE_STRING, or VALUE_NUMBER state",
                    )
                }
            }

        override fun isIntegralNumber(): Boolean {
            if (state == JsonParser.Event.VALUE_NUMBER) {
                return (scope.getValue() as JsonNumber).isIntegral
            }
            throw IllegalStateException("Target json value must a number, not $state")
        }

        override fun getInt(): Int {
            if (state == JsonParser.Event.VALUE_NUMBER) {
                return (scope.getValue() as JsonNumber).intValue()
            }
            throw IllegalStateException("Target json value must a number, not $state")
        }

        override fun getLong(): Long {
            if (state == JsonParser.Event.VALUE_NUMBER) {
                return (scope.getValue() as JsonNumber).longValue()
            }
            throw IllegalStateException("Target json value must a number, not $state")
        }

        override fun getBigDecimal(): BigDecimal {
            if (state == JsonParser.Event.VALUE_NUMBER) {
                return (scope.getValue() as JsonNumber).bigDecimalValue()
            }
            throw IllegalStateException("Target json value must a number, not $state")
        }

        override fun getLocation(): JsonLocation = throw UnsupportedOperationException("JSON-P adapter does not support getLocation()")

        override fun skipArray() {
            if (scope is JsonArrayScope) {
                while (scope.hasNext()) {
                    scope.next()
                }
                state = JsonParser.Event.END_ARRAY
            }
        }

        override fun skipObject() {
            if (scope is JsonObjectScope) {
                while (scope.hasNext()) {
                    scope.next()
                }
                state = JsonParser.Event.END_OBJECT
            }
        }

        override fun close() {
        }

        private fun createScope(value: JsonValue?): JsonStructureScope {
            if (value is JsonArray) {
                return JsonArrayScope(value)
            } else if (value is JsonObject) {
                return JsonObjectScope(value)
            }
            throw JsonException("Cannot create JSON iterator for $value")
        }

        private fun getState(value: JsonValue?): JsonParser.Event =
            when (checkNotNull(value) { "JSON event requires a current value" }.valueType) {
                JsonValue.ValueType.ARRAY -> JsonParser.Event.START_ARRAY
                JsonValue.ValueType.OBJECT -> JsonParser.Event.START_OBJECT
                JsonValue.ValueType.STRING -> JsonParser.Event.VALUE_STRING
                JsonValue.ValueType.NUMBER -> JsonParser.Event.VALUE_NUMBER
                JsonValue.ValueType.TRUE -> JsonParser.Event.VALUE_TRUE
                JsonValue.ValueType.FALSE -> JsonParser.Event.VALUE_FALSE
                JsonValue.ValueType.NULL -> JsonParser.Event.VALUE_NULL
                else -> throw JsonException("Unknown value type ${value.valueType}")
            }
    }

    private abstract class JsonStructureScope : Iterator<JsonValue> {
        abstract fun getValue(): JsonValue
    }

    private class JsonArrayScope(
        array: JsonArray,
    ) : JsonStructureScope() {
        private val it: Iterator<JsonValue> = array.iterator()
        private var value: JsonValue? = null

        override fun hasNext(): Boolean = it.hasNext()

        override fun next(): JsonValue {
            value = it.next()
            return checkNotNull(value) { "JSON iterator yielded no value" }
        }

        override fun getValue(): JsonValue = checkNotNull(value) { "JSON iterator has not advanced" }
    }

    private class JsonObjectScope(
        jsonObject: JsonObject,
    ) : JsonStructureScope() {
        private val it: Iterator<Map.Entry<String, JsonValue>> =
            jsonObject.entries.iterator()
        private var value: JsonValue? = null
        private var key: String? = null

        override fun hasNext(): Boolean = it.hasNext()

        override fun next(): JsonValue {
            val next = it.next()
            key = next.key
            value = next.value
            return checkNotNull(value) { "JSON iterator yielded no value" }
        }

        override fun getValue(): JsonValue = checkNotNull(value) { "JSON iterator has not advanced" }

        fun getKey(): String = checkNotNull(key) { "JSON object iterator has no current key" }
    }
}
