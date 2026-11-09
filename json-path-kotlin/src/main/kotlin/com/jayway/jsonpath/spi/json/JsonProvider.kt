package com.jayway.jsonpath.spi.json

interface JsonProvider {
    fun parse(json: String): Any?
}
