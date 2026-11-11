package com.jayway.jsonpath.internal.path
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.EvaluationListener
import com.jayway.jsonpath.Option
import com.jayway.jsonpath.PathNotFoundException
import com.jayway.jsonpath.internal.EvaluationAbortException
import com.jayway.jsonpath.internal.EvaluationContext
import com.jayway.jsonpath.internal.Path
import com.jayway.jsonpath.internal.PathRef
import com.jayway.jsonpath.internal.Utils
import com.jayway.jsonpath.spi.json.JsonProvider
import java.util.Collections
import java.util.HashMap

open class EvaluationContextImpl(
    path: Path,
    rootDocument: Any,
    configuration: Configuration,
    private val forUpdate: Boolean,
) : EvaluationContext {
    private val configuration: Configuration
    private val valueResult: Any
    private val pathResult: Any
    private val path: Path
    private val rootDocument: Any?
    private val updateOperations: MutableList<PathRef>
    private val documentEvalCache = HashMap<Path, Any?>()
    private val suppressExceptions: Boolean
    private var resultIndex = 0

    init {
        Utils.notNull(path, "path can not be null")
        Utils.notNull(rootDocument, "root can not be null")
        Utils.notNull(configuration, "configuration can not be null")
        this.path = path
        this.rootDocument = rootDocument
        this.configuration = configuration
        valueResult = checkNotNull(configuration.jsonProvider().createArray()) { "JsonProvider returned null instead of a result array" }
        pathResult = checkNotNull(configuration.jsonProvider().createArray()) { "JsonProvider returned null instead of a path array" }
        updateOperations = ArrayList()
        suppressExceptions = configuration.containsOption(Option.SUPPRESS_EXCEPTIONS)
    }

    open fun getRoot(): RootPathToken = (path as CompiledPath).getRoot()

    open fun documentEvalCache(): HashMap<Path, Any?> = documentEvalCache

    open fun forUpdate(): Boolean = forUpdate

    open fun addResult(
        path: String,
        operation: PathRef,
        model: Any?,
    ) {
        if (forUpdate) updateOperations.add(operation)
        configuration.jsonProvider().setArrayIndex(valueResult, resultIndex, model)
        configuration.jsonProvider().setArrayIndex(pathResult, resultIndex, path)
        resultIndex++
        if (configuration().getEvaluationListeners().isNotEmpty()) {
            val idx = resultIndex - 1
            for (listener in configuration().getEvaluationListeners()) {
                val continuation = listener.resultFound(FoundResultImpl(idx, path, model))
                if (EvaluationListener.EvaluationContinuation.ABORT == continuation) throw ABORT_EVALUATION
            }
        }
    }

    open fun jsonProvider(): JsonProvider = configuration.jsonProvider()

    open fun options(): Set<Option> = configuration.getOptions()

    override fun configuration(): Configuration = configuration

    override fun rootDocument(): Any? = rootDocument

    override fun updateOperations(): Collection<PathRef> {
        Collections.sort(updateOperations)
        return Collections.unmodifiableCollection(updateOperations)
    }

    override fun <T> getValue(): T? = getValue(true)

    @Suppress("UNCHECKED_CAST")
    override fun <T> getValue(unwrap: Boolean): T? {
        if (path.isDefinite()) {
            if (resultIndex == 0) {
                if (suppressExceptions) return null
                throw PathNotFoundException("No results for path: " + path.toString())
            }
            val len = jsonProvider().length(valueResult)
            var value = if (len > 0) jsonProvider().getArrayIndex(valueResult, len - 1) else null
            if (value != null && unwrap) value = jsonProvider().unwrap(value)
            return value as T?
        }
        return valueResult as T?
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getPath(): T? {
        if (resultIndex == 0) {
            if (suppressExceptions) return null
            throw PathNotFoundException("No results for path: " + path.toString())
        }
        return pathResult as T?
    }

    override fun getPathList(): List<String> {
        val res = ArrayList<String>()
        if (resultIndex > 0) {
            val objects = configuration.jsonProvider().toIterable(pathResult)
            for (o in objects) res.add(o as String)
        }
        return res
    }

    private open class FoundResultImpl(
        private val index: Int,
        private val path: String,
        private val result: Any?,
    ) : EvaluationListener.FoundResult {
        override fun index(): Int = index

        override fun path(): String = path

        override fun result(): Any? = result
    }

    companion object {
        private val ABORT_EVALUATION = EvaluationAbortException()
    }
}
