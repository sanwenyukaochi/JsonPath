package com.jayway.jsonpath
import com.jayway.jsonpath.internal.EvaluationContext
import com.jayway.jsonpath.internal.ParseContextImpl
import com.jayway.jsonpath.internal.Path
import com.jayway.jsonpath.internal.Utils
import com.jayway.jsonpath.internal.path.PathCompiler
import com.jayway.jsonpath.spi.json.JsonProvider
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL

@Suppress("UNCHECKED_CAST", "DEPRECATION")
open class JsonPath private constructor(
    jsonPath: String,
    filters: Array<out Predicate>,
) {
    private val path: Path

    init {
        Utils.notNull(jsonPath, "path can not be null")
        path = PathCompiler.compile(jsonPath, *filters)
    }

    open fun getPath(): String = path.toString()

    open fun isDefinite(): Boolean = path.isDefinite()

    open fun <T> read(jsonObject: Any): T? = read(jsonObject, Configuration.defaultConfiguration())

    open fun <T> read(
        jsonObject: Any,
        configuration: Configuration,
    ): T? {
        val optAsPathList = configuration.containsOption(Option.AS_PATH_LIST)
        val optAlwaysReturnList = configuration.containsOption(Option.ALWAYS_RETURN_LIST)
        val optSuppressExceptions = configuration.containsOption(Option.SUPPRESS_EXCEPTIONS)
        if (path.isFunctionPath()) {
            if (optAsPathList || optAlwaysReturnList) {
                if (optSuppressExceptions) return (if (path.isDefinite()) null else configuration.jsonProvider().createArray()) as T?
                throw JsonPathException(
                    "Options " + Option.AS_PATH_LIST + " and " + Option.ALWAYS_RETURN_LIST + " are not allowed when using path functions!",
                )
            }
            val evaluationContext = path.evaluate(jsonObject, jsonObject, configuration)
            if (optSuppressExceptions &&
                evaluationContext.getPathList().isEmpty()
            ) {
                return (if (path.isDefinite()) null else configuration.jsonProvider().createArray()) as T?
            }
            return evaluationContext.getValue(true)
        } else if (optAsPathList) {
            val evaluationContext = path.evaluate(jsonObject, jsonObject, configuration)
            if (optSuppressExceptions && evaluationContext.getPathList().isEmpty()) return configuration.jsonProvider().createArray() as T?
            return evaluationContext.getPath()
        } else {
            val evaluationContext = path.evaluate(jsonObject, jsonObject, configuration)
            if (optSuppressExceptions && evaluationContext.getPathList().isEmpty()) {
                if (optAlwaysReturnList) {
                    return configuration.jsonProvider().createArray() as T?
                } else {
                    return (if (path.isDefinite()) null else configuration.jsonProvider().createArray()) as T?
                }
            }
            val res: Any? = evaluationContext.getValue(false)
            if (optAlwaysReturnList && path.isDefinite()) {
                val array = configuration.jsonProvider().createArray()
                configuration.jsonProvider().setArrayIndex(array, 0, res)
                return array as T?
            } else {
                return res as T?
            }
        }
    }

    open fun <T> set(
        jsonObject: Any,
        newVal: Any?,
        configuration: Configuration,
    ): T? {
        Utils.notNull(jsonObject, "json can not be null")

        Utils.notNull(configuration, "configuration can not be null")

        val evaluationContext = path.evaluate(jsonObject, jsonObject, configuration, true)
        if (evaluationContext.getPathList().isEmpty()) {
            val optSuppressExceptions = configuration.containsOption(Option.SUPPRESS_EXCEPTIONS)
            if (optSuppressExceptions) return handleMissingPathInContext(configuration) else throw PathNotFoundException()
        }
        for (updateOperation in evaluationContext.updateOperations()) updateOperation.set(newVal, configuration)
        return resultByConfiguration(jsonObject, configuration, evaluationContext)
    }

    open fun <T> map(
        jsonObject: Any,
        mapFunction: MapFunction,
        configuration: Configuration,
    ): T? {
        Utils.notNull(jsonObject, "json can not be null")

        Utils.notNull(configuration, "configuration can not be null")
        Utils.notNull(mapFunction, "mapFunction can not be null")
        val evaluationContext = path.evaluate(jsonObject, jsonObject, configuration, true)
        if (evaluationContext.getPathList().isEmpty()) {
            val optSuppressExceptions = configuration.containsOption(Option.SUPPRESS_EXCEPTIONS)
            if (optSuppressExceptions) return handleMissingPathInContext(configuration) else throw PathNotFoundException()
        }
        for (updateOperation in evaluationContext.updateOperations()) updateOperation.convert(mapFunction, configuration)
        return resultByConfiguration(jsonObject, configuration, evaluationContext)
    }

    open fun <T> delete(
        jsonObject: Any,
        configuration: Configuration,
    ): T? {
        Utils.notNull(jsonObject, "json can not be null")

        Utils.notNull(configuration, "configuration can not be null")

        val evaluationContext = path.evaluate(jsonObject, jsonObject, configuration, true)
        if (evaluationContext.getPathList().isEmpty()) {
            val optSuppressExceptions = configuration.containsOption(Option.SUPPRESS_EXCEPTIONS)
            if (optSuppressExceptions) return handleMissingPathInContext(configuration) else throw PathNotFoundException()
        }
        for (updateOperation in evaluationContext.updateOperations()) updateOperation.delete(configuration)
        return resultByConfiguration(jsonObject, configuration, evaluationContext)
    }

    open fun <T> add(
        jsonObject: Any,
        value: Any?,
        configuration: Configuration,
    ): T? {
        Utils.notNull(jsonObject, "json can not be null")

        Utils.notNull(configuration, "configuration can not be null")

        val evaluationContext = path.evaluate(jsonObject, jsonObject, configuration, true)
        if (evaluationContext.getPathList().isEmpty()) {
            val optSuppressExceptions = configuration.containsOption(Option.SUPPRESS_EXCEPTIONS)
            if (optSuppressExceptions) return handleMissingPathInContext(configuration) else throw PathNotFoundException()
        }
        for (updateOperation in evaluationContext.updateOperations()) updateOperation.add(value, configuration)
        return resultByConfiguration(jsonObject, configuration, evaluationContext)
    }

    open fun <T> put(
        jsonObject: Any,
        key: String,
        value: Any?,
        configuration: Configuration,
    ): T? {
        Utils.notNull(jsonObject, "json can not be null")
        Utils.notEmpty(key, "key can not be null or empty")
        Utils.notNull(configuration, "configuration can not be null")

        val evaluationContext = path.evaluate(jsonObject, jsonObject, configuration, true)
        if (evaluationContext.getPathList().isEmpty()) {
            val optSuppressExceptions = configuration.containsOption(Option.SUPPRESS_EXCEPTIONS)
            if (optSuppressExceptions) return handleMissingPathInContext(configuration) else throw PathNotFoundException()
        }
        for (updateOperation in evaluationContext.updateOperations()) updateOperation.put(key, value, configuration)
        return resultByConfiguration(jsonObject, configuration, evaluationContext)
    }

    open fun <T> renameKey(
        jsonObject: Any,
        oldKeyName: String,
        newKeyName: String,
        configuration: Configuration,
    ): T? {
        Utils.notNull(jsonObject, "json can not be null")
        Utils.notEmpty(newKeyName, "newKeyName can not be null or empty")
        Utils.notNull(configuration, "configuration can not be null")
        val evaluationContext = path.evaluate(jsonObject, jsonObject, configuration, true)
        for (updateOperation in evaluationContext.updateOperations()) {
            val optSuppressExceptions = configuration.containsOption(Option.SUPPRESS_EXCEPTIONS)
            try {
                updateOperation.renameKey(oldKeyName, newKeyName, configuration)
            } catch (
                e: RuntimeException,
            ) {
                if (!optSuppressExceptions) throw e
            }
        }
        return resultByConfiguration(jsonObject, configuration, evaluationContext)
    }

    open fun <T> read(json: String): T? = read(json, Configuration.defaultConfiguration())

    open fun <T> read(
        json: String,
        configuration: Configuration,
    ): T? {
        Utils.notEmpty(json, "json can not be null or empty")
        Utils.notNull(configuration, "jsonProvider can not be null")
        return read(requireNotNull(configuration.jsonProvider().parse(json)) { "JSON document cannot be null" }, configuration)
    }

    @Throws(IOException::class)
    open fun <T> read(jsonURL: URL): T? = read(jsonURL as Any, Configuration.defaultConfiguration())

    @Throws(IOException::class)
    open fun <T> read(jsonFile: File): T? = read(jsonFile, Configuration.defaultConfiguration())

    @Throws(IOException::class)
    open fun <T> read(
        jsonFile: File,
        configuration: Configuration,
    ): T? {
        Utils.notNull(jsonFile, "json file can not be null")
        Utils.isTrue(jsonFile.exists(), "json file does not exist")
        Utils.notNull(configuration, "jsonProvider can not be null")
        var fis: FileInputStream? = null
        try {
            fis = FileInputStream(jsonFile)
            return read(fis, configuration)
        } finally {
            Utils.closeQuietly(fis)
        }
    }

    @Throws(IOException::class)
    open fun <T> read(jsonInputStream: InputStream): T? = read(jsonInputStream, Configuration.defaultConfiguration())

    @Throws(IOException::class)
    open fun <T> read(
        jsonInputStream: InputStream,
        configuration: Configuration,
    ): T? {
        Utils.notNull(jsonInputStream, "json input stream can not be null")
        Utils.notNull(configuration, "configuration can not be null")
        return read(jsonInputStream, "UTF-8", configuration)
    }

    @Throws(IOException::class)
    open fun <T> read(
        jsonInputStream: InputStream,
        charset: String,
        configuration: Configuration,
    ): T? {
        Utils.notNull(jsonInputStream, "json input stream can not be null")
        Utils.notNull(charset, "charset can not be null")
        Utils.notNull(configuration, "configuration can not be null")
        try {
            return read(
                requireNotNull(configuration.jsonProvider().parse(jsonInputStream, charset)) { "JSON document cannot be null" },
                configuration,
            )
        } finally {
            Utils.closeQuietly(jsonInputStream)
        }
    }

    private fun <T> resultByConfiguration(
        jsonObject: Any,
        configuration: Configuration,
        evaluationContext: EvaluationContext,
    ): T? = if (configuration.containsOption(Option.AS_PATH_LIST)) evaluationContext.getPathList() as T? else jsonObject as T?

    private fun <T> handleMissingPathInContext(configuration: Configuration): T? {
        val optAsPathList = configuration.containsOption(Option.AS_PATH_LIST)
        val optAlwaysReturnList = configuration.containsOption(Option.ALWAYS_RETURN_LIST)
        if (optAsPathList) {
            return configuration.jsonProvider().createArray() as T?
        } else if (optAlwaysReturnList) {
            return configuration.jsonProvider().createArray() as T?
        } else {
            return (if (path.isDefinite()) null else configuration.jsonProvider().createArray()) as T?
        }
    }

    companion object {
        @JvmStatic fun isPathDefinite(path: String): Boolean = compile(path).isDefinite()

        @JvmStatic fun compile(
            jsonPath: String,
            vararg filters: Predicate,
        ): JsonPath {
            Utils.notEmpty(jsonPath, "json can not be null or empty")
            return JsonPath(jsonPath, filters)
        }

        @JvmStatic fun using(configuration: Configuration): ParseContext = ParseContextImpl(configuration)

        @Deprecated("Use a Configuration")
        @JvmStatic
        fun using(provider: JsonProvider): ParseContext = ParseContextImpl(Configuration.builder().jsonProvider(provider).build())

        @JvmStatic fun <T> read(
            json: Any,
            jsonPath: String,
            vararg filters: Predicate,
        ): T? = parse(json).read(jsonPath, *filters)

        @JvmStatic fun parse(json: Any): DocumentContext = ParseContextImpl().parse(json)

        @JvmStatic fun parse(
            json: Any,
            configuration: Configuration,
        ): DocumentContext = ParseContextImpl(configuration).parse(json)

        @JvmStatic fun <T> read(
            json: String,
            jsonPath: String,
            vararg filters: Predicate,
        ): T? = ParseContextImpl().parse(json).read(jsonPath, *filters)

        @JvmStatic fun parse(json: String): DocumentContext = ParseContextImpl().parse(json)

        @JvmStatic fun parse(
            json: String,
            configuration: Configuration,
        ): DocumentContext = ParseContextImpl(configuration).parse(json)

        @Throws(IOException::class)
        @JvmStatic
        fun <T> read(
            json: InputStream,
            jsonPath: String,
            vararg filters: Predicate,
        ): T? = ParseContextImpl().parse(json).read(jsonPath, *filters)

        @JvmStatic fun parse(json: InputStream): DocumentContext = ParseContextImpl().parse(json)

        @JvmStatic fun parse(
            json: InputStream,
            configuration: Configuration,
        ): DocumentContext = ParseContextImpl(configuration).parse(json)

        @Throws(IOException::class)
        @JvmStatic
        fun <T> read(
            json: File,
            jsonPath: String,
            vararg filters: Predicate,
        ): T? = ParseContextImpl().parse(json).read(jsonPath, *filters)

        @Throws(IOException::class)
        @JvmStatic
        fun parse(json: File): DocumentContext = ParseContextImpl().parse(json)

        @Throws(IOException::class)
        @JvmStatic
        fun parse(
            json: File,
            configuration: Configuration,
        ): DocumentContext = ParseContextImpl(configuration).parse(json)

        @Deprecated("URL parsing is deprecated")
        @Throws(IOException::class)
        @JvmStatic
        fun <T> read(
            json: URL,
            jsonPath: String,
            vararg filters: Predicate,
        ): T? = ParseContextImpl().parse(json).read(jsonPath, *filters)

        @Deprecated("URL parsing is deprecated")
        @Throws(IOException::class)
        @JvmStatic
        fun parse(json: URL): DocumentContext = ParseContextImpl().parse(json)

        @Deprecated("URL parsing is deprecated")
        @Throws(IOException::class)
        @JvmStatic
        fun parse(
            json: URL,
            configuration: Configuration,
        ): DocumentContext = ParseContextImpl(configuration).parse(json)
    }
}
