package com.jayway.jsonpath.spi.json

import com.jayway.jsonpath.InvalidJsonException
import tools.jackson.core.JacksonException
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.ObjectReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringWriter
import java.nio.charset.StandardCharsets
import java.util.LinkedHashMap
import java.util.LinkedList

open class Jackson3JsonProvider(
    @JvmField protected var objectMapper: ObjectMapper,
    @JvmField protected var objectReader: ObjectReader,
) : AbstractJsonProvider() {
    constructor() : this(defaultObjectMapper, defaultObjectReader)

    constructor(objectMapper: ObjectMapper) :
        this(objectMapper, objectMapper.reader().forType(Any::class.java))

    open fun getObjectMapper(): ObjectMapper = objectMapper

    @Throws(InvalidJsonException::class)
    override fun parse(json: String): Any? =
        try {
            objectReader.readValue(json)
        } catch (exception: JacksonException) {
            throw InvalidJsonException(exception, json)
        }

    @Throws(InvalidJsonException::class)
    override fun parse(json: ByteArray): Any? =
        try {
            objectReader.readValue(json)
        } catch (exception: JacksonException) {
            throw InvalidJsonException(exception, String(json, StandardCharsets.UTF_8))
        }

    @Throws(InvalidJsonException::class)
    override fun parse(
        jsonStream: InputStream,
        charset: String,
    ): Any? =
        try {
            objectReader.readValue(InputStreamReader(jsonStream, charset))
        } catch (exception: IOException) {
            throw InvalidJsonException(exception)
        }

    override fun toJson(obj: Any?): String {
        val writer = StringWriter()
        try {
            objectMapper.writeValue(writer, obj)
            writer.flush()
            writer.close()
            return writer.buffer.toString()
        } catch (exception: IOException) {
            throw InvalidJsonException(exception)
        }
    }

    override fun createArray(): List<Any?> = LinkedList()

    override fun createMap(): Any = LinkedHashMap<String, Any?>()

    private companion object {
        private val defaultObjectMapper = ObjectMapper()
        private val defaultObjectReader = defaultObjectMapper.reader().forType(Any::class.java)
    }
}
