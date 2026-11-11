package com.jayway.jsonpath.internal.path
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.internal.EvaluationAbortException
import com.jayway.jsonpath.internal.EvaluationContext
import com.jayway.jsonpath.internal.Path
import com.jayway.jsonpath.internal.PathRef
import com.jayway.jsonpath.internal.function.ParamType
import com.jayway.jsonpath.internal.function.Parameter
import org.slf4j.LoggerFactory

open class CompiledPath(
    root: RootPathToken,
    private val isRootPath: Boolean,
) : Path {
    private val root: RootPathToken = invertScannerFunctionRelationship(root)

    override fun isRootPath(): Boolean = isRootPath

    @Suppress("USELESS_IS_CHECK")
    private fun invertScannerFunctionRelationship(path: RootPathToken): RootPathToken {
        if (path.isFunctionPath() && path.next() is ScanPathToken) {
            var token: PathToken = path
            var prior: PathToken? = null
            while (true) {
                token = token.next()
                if (token is FunctionPathToken) break
                prior = token
            }
            if (token is FunctionPathToken) {
                val previous = checkNotNull(prior) { "Scan function requires an input path" }
                previous.setNext(null)
                path.setTail(previous)
                val parameter = Parameter()
                parameter.setPath(CompiledPath(path, true))
                parameter.setType(ParamType.PATH)
                token.setParameters(java.util.Arrays.asList(parameter))
                val functionRoot = RootPathToken('$')
                functionRoot.setTail(token)
                functionRoot.setNext(token)
                return functionRoot
            }
        }
        return path
    }

    override fun evaluate(
        document: Any?,
        rootDocument: Any?,
        configuration: Configuration,
        forUpdate: Boolean,
    ): EvaluationContext {
        if (logger.isDebugEnabled) logger.debug("Evaluating path: {}", toString())
        val ctx = EvaluationContextImpl(this, requireNotNull(rootDocument) { "Root document cannot be null" }, configuration, forUpdate)
        try {
            val op = if (ctx.forUpdate()) PathRef.createRoot(rootDocument) else PathRef.NO_OP
            root.evaluate("", op, document, ctx)
        } catch (
            _: EvaluationAbortException,
        ) {
        }
        return ctx
    }

    override fun evaluate(
        document: Any?,
        rootDocument: Any?,
        configuration: Configuration,
    ): EvaluationContext = evaluate(document, rootDocument, configuration, false)

    override fun isDefinite(): Boolean = root.isPathDefinite()

    override fun isFunctionPath(): Boolean = root.isFunctionPath()

    override fun toString(): String = root.toString()

    open fun getRoot(): RootPathToken = root

    companion object {
        private val logger = LoggerFactory.getLogger(CompiledPath::class.java)
    }
}
