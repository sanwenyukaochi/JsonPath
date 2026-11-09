package com.jayway.jsonpath

import com.jayway.jsonpath.internal.JsonContext

class JsonPath private constructor() {
    companion object {
        @JvmStatic
        fun parse(json: String?): DocumentContext {
            require(!json.isNullOrEmpty()) {
                "json string can not be null or empty"
            }

            return JsonContext(json)
        }
    }
}
