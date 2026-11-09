package com.jayway.jsonpath

import com.jayway.jsonpath.internal.ParseContextImpl

class JsonPath private constructor() {
    companion object {
        @JvmStatic
        fun parse(json: String?): DocumentContext = ParseContextImpl().parse(json)
    }
}
