package com.jayway.jsonpath

import com.jayway.jsonpath.internal.DefaultsImpl
import com.jayway.jsonpath.internal.Utils
import com.jayway.jsonpath.spi.json.JsonProvider
import com.jayway.jsonpath.spi.mapper.MappingProvider
import java.util.Collections
import java.util.EnumSet
import java.util.Objects

open class Configuration private constructor(
    jsonProvider: JsonProvider?,
    mappingProvider: MappingProvider?,
    options: EnumSet<Option>?,
    evaluationListeners: Collection<@JvmSuppressWildcards EvaluationListener>?,
) {
    private val jsonProvider: JsonProvider =
        Utils.notNull(jsonProvider, "jsonProvider can not be null")
    private val mappingProvider: MappingProvider =
        Utils.notNull(mappingProvider, "mappingProvider can not be null")
    private val options: Set<Option> =
        Collections.unmodifiableSet(Utils.notNull(options, "setOptions can not be null"))
    private val evaluationListeners: Collection<@JvmSuppressWildcards EvaluationListener> =
        Collections.unmodifiableCollection(
            Utils.notNull(evaluationListeners, "evaluationListeners can not be null"),
        )

    open fun addEvaluationListeners(vararg evaluationListener: EvaluationListener): Configuration =
        builder()
            .jsonProvider(jsonProvider)
            .mappingProvider(mappingProvider)
            .options(options)
            .evaluationListener(*evaluationListener)
            .build()

    open fun setEvaluationListeners(vararg evaluationListener: EvaluationListener): Configuration =
        builder()
            .jsonProvider(jsonProvider)
            .mappingProvider(mappingProvider)
            .options(options)
            .evaluationListener(*evaluationListener)
            .build()

    open fun getEvaluationListeners(): Collection<EvaluationListener> = evaluationListeners

    open fun jsonProvider(newJsonProvider: JsonProvider?): Configuration =
        builder()
            .jsonProvider(newJsonProvider)
            .mappingProvider(mappingProvider)
            .options(options)
            .evaluationListener(evaluationListeners)
            .build()

    open fun jsonProvider(): JsonProvider = jsonProvider

    open fun mappingProvider(newMappingProvider: MappingProvider?): Configuration =
        builder()
            .jsonProvider(jsonProvider)
            .mappingProvider(newMappingProvider)
            .options(options)
            .evaluationListener(evaluationListeners)
            .build()

    open fun mappingProvider(): MappingProvider = mappingProvider

    open fun addOptions(vararg options: Option): Configuration {
        val newOptions = EnumSet.noneOf(Option::class.java)
        newOptions.addAll(this.options)
        newOptions.addAll(options.asList())
        return builder()
            .jsonProvider(jsonProvider)
            .mappingProvider(mappingProvider)
            .options(newOptions)
            .evaluationListener(evaluationListeners)
            .build()
    }

    open fun setOptions(vararg options: Option): Configuration =
        builder()
            .jsonProvider(jsonProvider)
            .mappingProvider(mappingProvider)
            .options(*options)
            .evaluationListener(evaluationListeners)
            .build()

    open fun getOptions(): Set<Option> = options

    open fun containsOption(option: Option?): Boolean = options.contains(option)

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        other as Configuration
        return jsonProvider.javaClass == other.jsonProvider.javaClass &&
            mappingProvider.javaClass == other.mappingProvider.javaClass &&
            Objects.equals(options, other.options)
    }

    open class ConfigurationBuilder {
        private var jsonProvider: JsonProvider? = null
        private var mappingProvider: MappingProvider? = null
        private var options: EnumSet<Option> = EnumSet.noneOf(Option::class.java)
        private var evaluationListener: Collection<@JvmSuppressWildcards EvaluationListener> =
            ArrayList()

        open fun jsonProvider(provider: JsonProvider?): ConfigurationBuilder {
            jsonProvider = provider
            return this
        }

        open fun mappingProvider(provider: MappingProvider?): ConfigurationBuilder {
            mappingProvider = provider
            return this
        }

        open fun options(vararg flags: Option): ConfigurationBuilder {
            if (flags.isNotEmpty()) {
                options.addAll(flags.asList())
            }
            return this
        }

        open fun options(options: Set<@JvmSuppressWildcards Option>): ConfigurationBuilder {
            this.options.addAll(options)
            return this
        }

        open fun evaluationListener(vararg listener: EvaluationListener): ConfigurationBuilder {
            evaluationListener = listener.asList()
            return this
        }

        open fun evaluationListener(listeners: Collection<@JvmSuppressWildcards EvaluationListener>?): ConfigurationBuilder {
            evaluationListener = listeners ?: emptyList()
            return this
        }

        open fun build(): Configuration {
            if (jsonProvider == null || mappingProvider == null) {
                val defaults = getEffectiveDefaults()
                if (jsonProvider == null) {
                    jsonProvider = defaults.jsonProvider()
                }
                if (mappingProvider == null) {
                    mappingProvider = defaults.mappingProvider()
                }
            }
            return Configuration(jsonProvider, mappingProvider, options, evaluationListener)
        }
    }

    interface Defaults {
        fun jsonProvider(): JsonProvider

        fun options(): Set<Option>

        fun mappingProvider(): MappingProvider
    }

    companion object {
        @Suppress("ktlint:standard:property-naming")
        private var DEFAULTS: Defaults? = null

        @JvmStatic
        @Synchronized
        fun setDefaults(defaults: Defaults?) {
            DEFAULTS = defaults
        }

        @JvmStatic
        private fun getEffectiveDefaults(): Defaults = DEFAULTS ?: DefaultsImpl.INSTANCE

        @JvmStatic
        fun defaultConfiguration(): Configuration {
            val defaults = getEffectiveDefaults()
            return builder()
                .jsonProvider(defaults.jsonProvider())
                .options(defaults.options())
                .build()
        }

        @JvmStatic
        fun builder(): ConfigurationBuilder = ConfigurationBuilder()
    }
}
