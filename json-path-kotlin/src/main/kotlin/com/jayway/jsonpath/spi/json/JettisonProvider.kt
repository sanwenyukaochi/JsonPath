package com.jayway.jsonpath.spi.json

import com.jayway.jsonpath.InvalidJsonException
import org.codehaus.jettison.json.JSONArray
import org.codehaus.jettison.json.JSONException
import org.codehaus.jettison.json.JSONObject
import org.codehaus.jettison.json.JSONTokener
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.Collections
import java.util.NoSuchElementException

open class JettisonProvider : AbstractJsonProvider() {
    private class JettisonTokener(
        value: String,
    ) : JSONTokener(value) {
        @Throws(JSONException::class)
        override fun newJSONObject(): JettisonObject = JettisonObject(this)

        @Throws(JSONException::class)
        override fun newJSONArray(): JettisonArray = JettisonArray(this)
    }

    private class JettisonObject :
        JSONObject,
        Iterable<Any?> {
        @Throws(JSONException::class)
        constructor(tokener: JettisonTokener) : super(tokener)

        constructor() : super()

        override fun iterator(): Iterator<Any?> = JettisonObjectIterator(this)

        private companion object {
            private const val serialVersionUID = 1L
        }
    }

    private class JettisonArray :
        JSONArray,
        Iterable<Any?> {
        @Throws(JSONException::class)
        constructor(tokener: JettisonTokener) : super(tokener)

        constructor() : super()

        override fun iterator(): Iterator<Any?> = JettisonArrayIterator(this)

        private companion object {
            private const val serialVersionUID = 2L
        }
    }

    private class JettisonArrayIterator(
        private val jsonArray: JSONArray,
    ) : MutableIterator<Any?> {
        private var index = 0

        override fun hasNext(): Boolean = index < jsonArray.length()

        override fun next(): Any? =
            try {
                jettisonUnwrap(jsonArray.get(index++))
            } catch (jsonException: JSONException) {
                throw NoSuchElementException(jsonException.toString())
            }

        override fun remove(): Unit = throw UnsupportedOperationException()
    }

    private class JettisonObjectIterator(
        private val jsonObject: JSONObject,
    ) : MutableIterator<Any?> {
        private val jsonKeysIterator: MutableIterator<*> = jsonObject.keys()

        override fun hasNext(): Boolean = jsonKeysIterator.hasNext()

        override fun next(): Any? =
            try {
                jettisonUnwrap(jsonObject.get(java.lang.String.valueOf(jsonKeysIterator.next())))
            } catch (jsonException: JSONException) {
                throw NoSuchElementException(jsonException.toString())
            }

        override fun remove() {
            jsonKeysIterator.remove()
        }
    }

    private fun parse(jsonTokener: JettisonTokener): Any =
        try {
            val nextCharacter = jsonTokener.nextClean()
            jsonTokener.back()
            if (nextCharacter == '{') {
                JettisonObject(jsonTokener)
            } else if (nextCharacter == '[') {
                JettisonArray(jsonTokener)
            } else {
                throw JSONException("Invalid JSON")
            }
        } catch (jsonException: JSONException) {
            throw IllegalStateException(jsonException)
        }

    @Throws(InvalidJsonException::class)
    override fun parse(json: String): Any = parse(JettisonTokener(json))

    @Throws(InvalidJsonException::class)
    override fun parse(
        jsonStream: InputStream,
        charset: String,
    ): Any =
        try {
            val stream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var size: Int
            while (jsonStream.read(buffer).also { size = it } > 0) {
                stream.write(buffer, 0, size)
            }
            parse(JettisonTokener(decode(stream.toByteArray(), charset)))
        } catch (ioException: IOException) {
            throw InvalidJsonException(ioException)
        }

    override fun toJson(obj: Any?): String =
        try {
            when (obj) {
                is JSONArray -> obj.toString(2)
                is JSONObject -> obj.toString(2)
                else -> java.lang.String.valueOf(obj)
            }
        } catch (jsonException: JSONException) {
            throw IllegalStateException(jsonException)
        }

    override fun createMap(): Any = JettisonObject()

    override fun createArray(): Iterable<*> = JettisonArray()

    override fun unwrap(obj: Any?): Any? = jettisonUnwrap(obj)

    override fun isArray(obj: Any?): Boolean = obj is JSONArray

    override fun isMap(obj: Any?): Boolean = obj is JSONObject

    override fun length(obj: Any?): Int =
        when (obj) {
            is JSONArray -> obj.length()
            is JSONObject -> obj.length()
            is String -> obj.length
            else -> 0
        }

    override fun toIterable(obj: Any?): Iterable<*> =
        object : Iterable<Any?> {
            override fun iterator(): Iterator<Any?> =
                when (obj) {
                    is JSONArray -> JettisonArrayIterator(obj)
                    is JSONObject -> JettisonObjectIterator(obj)
                    else -> Collections.emptyList<Any?>().iterator()
                }
        }

    override fun getPropertyKeys(obj: Any?): Collection<String> {
        val keys = ArrayList<String>(length(obj))
        if (obj is JSONArray) {
            for (index in 0 until length(obj)) {
                keys.add(java.lang.String.valueOf(index))
            }
        }
        if (obj is JSONObject) {
            val keysIterator = obj.keys()
            while (keysIterator.hasNext()) {
                keys.add(java.lang.String.valueOf(keysIterator.next()))
            }
        }
        return keys
    }

    override fun getArrayIndex(
        obj: Any?,
        idx: Int,
    ): Any? = jettisonUnwrap((obj as JSONArray).opt(idx))

    override fun setArrayIndex(
        array: Any?,
        idx: Int,
        newValue: Any?,
    ) {
        if (!isArray(array)) {
            throw UnsupportedOperationException()
        }
        try {
            (array as JSONArray).put(idx, jettisonWrap(newValue))
        } catch (jsonException: JSONException) {
            throw IllegalArgumentException(jsonException)
        }
    }

    override fun getMapValue(
        obj: Any?,
        key: String?,
    ): Any? {
        val value = (obj as JSONObject).opt(key)
        if (value == null) {
            return JsonProvider.UNDEFINED
        }
        return jettisonUnwrap(value)
    }

    override fun setProperty(
        obj: Any?,
        key: Any?,
        value: Any?,
    ) {
        try {
            if (obj is JSONArray) {
                val index =
                    if (key is Int) {
                        key
                    } else {
                        Integer.parseInt(
                            requireNotNull(key) { "A property key or array index is required" }.toString(),
                        )
                    }
                obj.put(index, jettisonWrap(value))
            }
            if (obj is JSONObject) {
                obj.put(java.lang.String.valueOf(key), jettisonWrap(value))
            }
        } catch (jsonException: JSONException) {
            throw IllegalStateException(jsonException)
        }
    }

    override fun removeProperty(
        obj: Any?,
        key: Any?,
    ) {
        try {
            if (obj is JSONArray) {
                val index =
                    if (key is Int) {
                        key
                    } else {
                        Integer.parseInt(
                            requireNotNull(key) { "A property key or array index is required" }.toString(),
                        )
                    }
                if (index < length(obj)) {
                    val temporaryValue = Any()
                    obj.put(index, temporaryValue)
                    obj.remove(temporaryValue)
                }
            }
            if (obj is JSONObject) {
                obj.remove(java.lang.String.valueOf(key))
            }
        } catch (jsonException: JSONException) {
            throw IllegalStateException(jsonException)
        }
    }

    private companion object {
        @JvmStatic
        @Throws(UnsupportedEncodingException::class)
        private fun decode(
            bytes: ByteArray,
            charset: String,
        ): String =
            try {
                String(bytes, Charset.forName(charset))
            } catch (_: IllegalArgumentException) {
                throw UnsupportedEncodingException(charset)
            }

        @JvmStatic
        private fun jettisonUnwrap(obj: Any?): Any? =
            if (obj != null && obj == JSONObject.NULL) {
                null
            } else {
                obj
            }

        @JvmStatic
        private fun jettisonWrap(obj: Any?): Any = obj ?: JSONObject.NULL
    }
}
