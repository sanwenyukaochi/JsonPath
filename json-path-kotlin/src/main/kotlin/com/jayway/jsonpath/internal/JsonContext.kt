package com.jayway.jsonpath.internal
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.EvaluationListener
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.MapFunction
import com.jayway.jsonpath.Option
import com.jayway.jsonpath.Predicate
import com.jayway.jsonpath.ReadContext
import com.jayway.jsonpath.TypeRef
import com.jayway.jsonpath.spi.cache.CacheProvider
import org.slf4j.LoggerFactory

@Suppress("UNCHECKED_CAST")
open class JsonContext(
    json: Any,
    configuration: Configuration,
) : DocumentContext {
    private val configuration: Configuration
    private val json: Any

    init {
        Utils.notNull(json, "json can not be null")
        Utils.notNull(configuration, "configuration can not be null")
        this.configuration = configuration
        this.json = json
    }

    override fun configuration(): Configuration = configuration

    override fun <T> json(): T = json as T

    override fun jsonString(): String = configuration.jsonProvider().toJson(json)

    override fun <T> read(
        path: String,
        vararg filters: Predicate,
    ): T? {
        Utils.notEmpty(path, "path can not be null or empty")
        return read(pathFromCache(path, filters))
    }

    override fun <T> read(
        path: String,
        type: Class<T>,
        vararg filters: Predicate,
    ): T? = convert(read<Any?>(path, *filters), type, configuration)

    override fun <T> read(path: JsonPath): T? {
        Utils.notNull(path, "path can not be null")
        return path.read(json, configuration)
    }

    override fun <T> read(
        path: JsonPath,
        type: Class<T>,
    ): T? = convert(read<Any?>(path), type, configuration)

    override fun <T> read(
        path: JsonPath,
        typeRef: TypeRef<T>,
    ): T? = convert(read<Any?>(path), typeRef, configuration)

    override fun <T> read(
        path: String,
        typeRef: TypeRef<T>,
    ): T? = convert(read<Any?>(path), typeRef, configuration)

    override fun limit(maxResults: Int): ReadContext = withListeners(LimitingEvaluationListener(maxResults))

    override fun withListeners(vararg listener: EvaluationListener): ReadContext =
        JsonContext(json, configuration.setEvaluationListeners(*listener))

    private fun <T> convert(
        obj: Any?,
        targetType: Class<T>,
        configuration: Configuration,
    ): T? = configuration.mappingProvider().map(obj, targetType, configuration) as T?

    private fun <T> convert(
        obj: Any?,
        targetType: TypeRef<T>,
        configuration: Configuration,
    ): T? = configuration.mappingProvider().map(obj, targetType, configuration) as T?

    override fun map(
        path: String,
        mapFunction: MapFunction,
        vararg filters: Predicate,
    ): DocumentContext {
        map(pathFromCache(path, filters), mapFunction)
        return this
    }

    override fun map(
        path: JsonPath,
        mapFunction: MapFunction,
    ): DocumentContext? {
        val obj: Any? = path.map(json, mapFunction, configuration)
        return if (obj == null) null else this
    }

    override fun set(
        path: String,
        newValue: Any?,
        vararg filters: Predicate,
    ): DocumentContext = set(pathFromCache(path, filters), newValue)

    override fun set(
        path: JsonPath,
        newValue: Any?,
    ): DocumentContext {
        val modified: List<String> =
            checkNotNull(path.set<List<String>>(json, newValue, configuration.addOptions(Option.AS_PATH_LIST))) {
                "AS_PATH_LIST must produce a path list"
            }
        if (logger.isDebugEnabled) for (p in modified) logger.debug("Set path {} new value {}", p, newValue)
        return this
    }

    override fun delete(
        path: String,
        vararg filters: Predicate,
    ): DocumentContext = delete(pathFromCache(path, filters))

    override fun delete(path: JsonPath): DocumentContext {
        val modified: List<String> =
            checkNotNull(
                path.delete<List<String>>(json, configuration.addOptions(Option.AS_PATH_LIST)),
            ) { "AS_PATH_LIST must produce a path list" }
        if (logger.isDebugEnabled) for (p in modified) logger.debug("Delete path {}", p)
        return this
    }

    override fun add(
        path: String,
        value: Any?,
        vararg filters: Predicate,
    ): DocumentContext = add(pathFromCache(path, filters), value)

    override fun add(
        path: JsonPath,
        value: Any?,
    ): DocumentContext {
        val modified: List<String> =
            checkNotNull(
                path.add<List<String>>(json, value, configuration.addOptions(Option.AS_PATH_LIST)),
            ) { "AS_PATH_LIST must produce a path list" }
        if (logger.isDebugEnabled) for (p in modified) logger.debug("Add path {} new value {}", p, value)
        return this
    }

    override fun put(
        path: String,
        key: String,
        value: Any?,
        vararg filters: Predicate,
    ): DocumentContext = put(pathFromCache(path, filters), key, value)

    override fun put(
        path: JsonPath,
        key: String,
        value: Any?,
    ): DocumentContext {
        val modified: List<String> =
            checkNotNull(path.put<List<String>>(json, key, value, configuration.addOptions(Option.AS_PATH_LIST))) {
                "AS_PATH_LIST must produce a path list"
            }
        if (logger.isDebugEnabled) for (p in modified) logger.debug("Put path {} key {} value {}", p, key, value)
        return this
    }

    override fun renameKey(
        path: String,
        oldKeyName: String,
        newKeyName: String,
        vararg filters: Predicate,
    ): DocumentContext = renameKey(pathFromCache(path, filters), oldKeyName, newKeyName)

    override fun renameKey(
        path: JsonPath,
        oldKeyName: String,
        newKeyName: String,
    ): DocumentContext {
        val modified: List<String> =
            checkNotNull(path.renameKey<List<String>>(json, oldKeyName, newKeyName, configuration.addOptions(Option.AS_PATH_LIST))) {
                "AS_PATH_LIST must produce a path list"
            }
        if (logger.isDebugEnabled) for (p in modified) logger.debug("Rename path {} new value {}", p, newKeyName)
        return this
    }

    private fun pathFromCache(
        path: String,
        filters: Array<out Predicate>?,
    ): JsonPath {
        val cache = CacheProvider.getCache()
        val cacheKey = if (filters == null || filters.isEmpty()) path else Utils.concat(path, java.util.Arrays.toString(filters))
        var jsonPath = cache.get(cacheKey)
        if (jsonPath == null) {
            jsonPath = JsonPath.compile(path, *(filters ?: emptyArray()))
            cache.put(cacheKey, jsonPath)
        }
        return jsonPath
    }

    private class LimitingEvaluationListener(
        @JvmField val limit: Int,
    ) : EvaluationListener {
        override fun resultFound(found: EvaluationListener.FoundResult): EvaluationListener.EvaluationContinuation =
            if (found.index() ==
                limit - 1
            ) {
                EvaluationListener.EvaluationContinuation.ABORT
            } else {
                EvaluationListener.EvaluationContinuation.CONTINUE
            }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JsonContext::class.java)
    }
}
