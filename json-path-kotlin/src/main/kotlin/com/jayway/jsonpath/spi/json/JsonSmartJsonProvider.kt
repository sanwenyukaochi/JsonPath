package com.jayway.jsonpath.spi.json

import com.jayway.jsonpath.InvalidJsonException
import com.jayway.jsonpath.JsonPathException
import net.minidev.json.JSONArray
import net.minidev.json.JSONObject
import net.minidev.json.JSONStyle
import net.minidev.json.JSONValue
import net.minidev.json.parser.JSONParser
import net.minidev.json.parser.ParseException
import net.minidev.json.writer.JsonReaderI
import java.io.InputStream
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException

open class JsonSmartJsonProvider : AbstractJsonProvider {
    private val parseMode: Int
    private val mapper: JsonReaderI<*>

    constructor() : this(JSONParser.MODE_PERMISSIVE, JSONValue.defaultReader.DEFAULT_ORDERED)

    constructor(parseMode: Int) : this(parseMode, JSONValue.defaultReader.DEFAULT_ORDERED)

    constructor(
        parseMode: Int,
        mapper: JsonReaderI<*>,
    ) : super() {
        this.parseMode = parseMode
        this.mapper = mapper
    }

    override fun createArray(): Any? = mapper.createArray()

    override fun createMap(): Any? = mapper.createObject()

    override fun parse(json: String): Any? =
        try {
            createParser().parse(json, mapper)
        } catch (exception: ParseException) {
            throw InvalidJsonException(exception)
        }

    @Throws(InvalidJsonException::class)
    override fun parse(
        jsonStream: InputStream,
        charset: String,
    ): Any? =
        try {
            createParser().parse(InputStreamReader(jsonStream, charset), mapper)
        } catch (exception: ParseException) {
            throw InvalidJsonException(exception)
        } catch (exception: UnsupportedEncodingException) {
            throw JsonPathException(exception)
        }

    @Suppress("UNCHECKED_CAST")
    override fun toJson(obj: Any?): String =
        if (obj is Map<*, *>) {
            JSONObject.toJSONString(obj as Map<String, *>, JSONStyle.LT_COMPRESS)
        } else if (obj is List<*>) {
            JSONArray.toJSONString(obj, JSONStyle.LT_COMPRESS)
        } else if (obj is Number || obj is Boolean) {
            JSONValue.toJSONString(obj)
        } else {
            throw UnsupportedOperationException(
                "${obj?.javaClass?.name ?: "null"} can not be converted to JSON",
            )
        }

    private fun createParser(): JSONParser = JSONParser(parseMode)
}
