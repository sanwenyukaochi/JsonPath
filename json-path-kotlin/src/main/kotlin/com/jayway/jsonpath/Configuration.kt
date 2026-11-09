package com.jayway.jsonpath

import com.jayway.jsonpath.spi.json.JsonProvider
import com.jayway.jsonpath.spi.json.JsonSmartJsonProvider

class Configuration private constructor(
    internal val jsonProvider: JsonProvider,
) {
    companion object {
        @JvmStatic
        fun defaultConfiguration(): Configuration = Configuration(JsonSmartJsonProvider())
    }
}
