package com.jayway.jsonpath.internal
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.ParseContext
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL

open class ParseContextImpl(
    private val configuration: Configuration,
) : ParseContext {
    constructor() : this(Configuration.defaultConfiguration())

    override fun parse(json: Any): DocumentContext {
        Utils.notNull(json, "json object can not be null")
        return JsonContext(json, configuration)
    }

    override fun parse(json: String): DocumentContext {
        Utils.notEmpty(json, "json string can not be null or empty")
        val obj = configuration.jsonProvider().parse(json)
        return JsonContext(requireNotNull(obj) { "JSON document cannot be null" }, configuration)
    }

    override fun parseUtf8(json: ByteArray): DocumentContext {
        Utils.notEmpty(json, "json bytes can not be null or empty")
        val obj = configuration.jsonProvider().parse(json)
        return JsonContext(requireNotNull(obj) { "JSON document cannot be null" }, configuration)
    }

    override fun parse(json: InputStream): DocumentContext = parse(json, "UTF-8")

    override fun parse(
        json: InputStream,
        charset: String,
    ): DocumentContext {
        Utils.notNull(json, "json input stream can not be null")
        Utils.notNull(charset, "charset can not be null")
        try {
            val obj = configuration.jsonProvider().parse(json, charset)
            return JsonContext(requireNotNull(obj) { "JSON document cannot be null" }, configuration)
        } finally {
            Utils.closeQuietly(json)
        }
    }

    @Throws(IOException::class)
    override fun parse(json: File): DocumentContext {
        Utils.notNull(json, "json file can not be null")
        var fis: FileInputStream? = null
        try {
            fis = FileInputStream(json)
            return parse(fis)
        } finally {
            Utils.closeQuietly(fis)
        }
    }

    @Deprecated("URL parsing is deprecated")
    @Throws(IOException::class)
    override fun parse(json: URL): DocumentContext {
        Utils.notNull(json, "url can not be null")
        var fis: InputStream? = null
        try {
            fis = json.openStream()
            return parse(fis)
        } finally {
            Utils.closeQuietly(fis)
        }
    }
}
