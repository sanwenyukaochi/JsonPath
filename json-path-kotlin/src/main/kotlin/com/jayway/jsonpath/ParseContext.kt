package com.jayway.jsonpath

interface ParseContext {
    fun parse(json: String?): DocumentContext
}
