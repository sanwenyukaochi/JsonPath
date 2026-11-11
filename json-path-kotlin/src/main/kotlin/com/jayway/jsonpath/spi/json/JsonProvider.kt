package com.jayway.jsonpath.spi.json

import com.jayway.jsonpath.InvalidJsonException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import kotlin.jvm.JvmDefaultWithoutCompatibility

@JvmDefaultWithoutCompatibility
interface JsonProvider {
    @Throws(InvalidJsonException::class)
    fun parse(json: String): Any?

    @Throws(InvalidJsonException::class)
    fun parse(json: ByteArray): Any? = parse(String(json, StandardCharsets.UTF_8))

    @Throws(InvalidJsonException::class)
    fun parse(
        jsonStream: InputStream,
        charset: String,
    ): Any?

    fun toJson(obj: Any?): String

    fun createArray(): Any?

    fun createMap(): Any?

    fun isArray(obj: Any?): Boolean

    fun length(obj: Any?): Int

    fun toIterable(obj: Any?): Iterable<*>

    fun getPropertyKeys(obj: Any?): Collection<String>

    fun getArrayIndex(
        obj: Any?,
        idx: Int,
    ): Any?

    @Deprecated("Use getArrayIndex(obj, idx) instead")
    fun getArrayIndex(
        obj: Any?,
        idx: Int,
        unwrap: Boolean,
    ): Any?

    fun setArrayIndex(
        array: Any?,
        idx: Int,
        newValue: Any?,
    )

    fun getMapValue(
        obj: Any?,
        key: String?,
    ): Any?

    fun setProperty(
        obj: Any?,
        key: Any?,
        value: Any?,
    )

    fun removeProperty(
        obj: Any?,
        key: Any?,
    )

    fun isMap(obj: Any?): Boolean

    fun unwrap(obj: Any?): Any?

    companion object {
        @JvmField
        val UNDEFINED: Any = Any()
    }
}
