package com.jayway.jsonpath

interface DocumentContext {
    fun <T> read(path: String): T
}
