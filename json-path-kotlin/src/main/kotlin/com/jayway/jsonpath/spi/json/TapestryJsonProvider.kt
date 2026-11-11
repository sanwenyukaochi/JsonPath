package com.jayway.jsonpath.spi.json

import com.jayway.jsonpath.InvalidJsonException
import org.apache.tapestry5.json.JSONArray
import org.apache.tapestry5.json.JSONCollection
import org.apache.tapestry5.json.JSONObject
import java.io.InputStream
import java.util.Scanner

@Suppress("DEPRECATION")
open class TapestryJsonProvider : AbstractJsonProvider() {
    @Throws(InvalidJsonException::class)
    override fun parse(json: String): Any = JSONObject(json)

    @Throws(InvalidJsonException::class)
    override fun parse(
        jsonStream: InputStream,
        charset: String,
    ): Any {
        var scanner: Scanner? = null
        try {
            scanner = Scanner(jsonStream, charset)
            return parse(scanner.useDelimiter("\\A").next())
        } finally {
            scanner?.close()
        }
    }

    override fun toJson(obj: Any?): String = requireNotNull(obj as? JSONCollection) { "Expected a JSONCollection" }.toCompactString()

    override fun createArray(): Any = JSONArray()

    override fun createMap(): Any = JSONObject()

    override fun setProperty(
        obj: Any?,
        key: Any?,
        value: Any?,
    ) {
        val actualValue = value ?: JSONObject.NULL
        if (isMap(obj)) {
            (obj as JSONObject).put(requireNotNull(key) { "A property key or array index is required" }.toString(), actualValue)
        }
    }

    override fun isMap(obj: Any?): Boolean = obj is JSONObject

    override fun getArrayIndex(
        obj: Any?,
        idx: Int,
    ): Any? = requireNotNull(obj as? JSONArray) { "Expected a JSONArray" }[idx]

    override fun getPropertyKeys(obj: Any?): Collection<String> = requireNotNull(obj as? JSONObject) { "Expected a JSONObject" }.keys()

    override fun getMapValue(
        obj: Any?,
        key: String?,
    ): Any? {
        val json = requireNotNull(obj as? JSONObject) { "Expected a JSONObject" }
        if (!json.has(key)) {
            return JsonProvider.UNDEFINED
        }
        return json[key]
    }

    override fun length(obj: Any?): Int {
        if (obj is JSONArray) {
            return obj.length()
        } else if (obj is JSONObject) {
            return obj.length()
        }
        throw IllegalArgumentException("Cannot determine length of $obj, unsupported type.")
    }

    override fun isArray(obj: Any?): Boolean = obj is JSONArray

    override fun setArrayIndex(
        array: Any?,
        idx: Int,
        newValue: Any?,
    ) {
        val actualValue = newValue ?: JSONObject.NULL
        val list = requireNotNull(array as? JSONArray) { "Expected a JSONArray" }
        list.put(idx, actualValue)
    }

    companion object {
        @JvmField
        val INSTANCE = TapestryJsonProvider()
    }
}
