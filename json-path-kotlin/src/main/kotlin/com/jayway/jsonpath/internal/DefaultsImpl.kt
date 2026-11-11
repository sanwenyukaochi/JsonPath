package com.jayway.jsonpath.internal

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.Option
import com.jayway.jsonpath.spi.json.JsonProvider
import com.jayway.jsonpath.spi.json.JsonSmartJsonProvider
import com.jayway.jsonpath.spi.mapper.JsonSmartMappingProvider
import com.jayway.jsonpath.spi.mapper.MappingProvider
import java.util.EnumSet

class DefaultsImpl private constructor() : Configuration.Defaults {
    private val mappingProvider: MappingProvider = JsonSmartMappingProvider()

    override fun jsonProvider(): JsonProvider = JsonSmartJsonProvider()

    override fun options(): Set<Option> = EnumSet.noneOf(Option::class.java)

    override fun mappingProvider(): MappingProvider = mappingProvider

    companion object {
        @JvmField
        val INSTANCE = DefaultsImpl()
    }
}
