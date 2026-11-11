package com.jayway.jsonpath
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URL

interface ParseContext {
    fun parse(json: String): DocumentContext

    fun parse(json: Any): DocumentContext

    fun parse(json: InputStream): DocumentContext

    fun parse(
        json: InputStream,
        charset: String,
    ): DocumentContext

    @Throws(IOException::class)
    fun parse(json: File): DocumentContext

    fun parseUtf8(json: ByteArray): DocumentContext

    @Deprecated("URL parsing is deprecated")
    @Throws(IOException::class)
    fun parse(json: URL): DocumentContext
}
