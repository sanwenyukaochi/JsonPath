package com.jayway.jsonpath.spi.json

import com.jayway.jsonpath.InvalidJsonException
import net.minidev.json.JSONValue
import net.minidev.json.parser.JSONParser
import net.minidev.json.parser.ParseException
import net.minidev.json.writer.JsonReaderI

class JsonSmartJsonProvider : JsonProvider {
    private val mapper: JsonReaderI<*> = JSONValue.defaultReader.DEFAULT_ORDERED

    override fun parse(json: String): Any? =
        try {
            JSONParser(JSONParser.MODE_PERMISSIVE).parse(json, mapper)
        } catch (exception: ParseException) {
            throw InvalidJsonException(exception)
        }
}
