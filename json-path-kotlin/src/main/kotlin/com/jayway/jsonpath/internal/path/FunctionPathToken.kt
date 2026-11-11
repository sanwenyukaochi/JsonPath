package com.jayway.jsonpath.internal.path
import com.jayway.jsonpath.internal.PathRef
import com.jayway.jsonpath.internal.function.ParamType
import com.jayway.jsonpath.internal.function.Parameter
import com.jayway.jsonpath.internal.function.PathFunctionFactory
import com.jayway.jsonpath.internal.function.latebinding.JsonLateBindingValue
import com.jayway.jsonpath.internal.function.latebinding.PathLateBindingValue

open class FunctionPathToken(
    pathFragment: String,
    parameters: List<@JvmSuppressWildcards Parameter>?,
) : PathToken() {
    private val functionName: String
    private val pathFragment: String = pathFragment + if (parameters != null && parameters.size > 0) "(...)" else "()"
    private var functionParams: List<@JvmSuppressWildcards Parameter>?

    init {
        functionName = pathFragment
        functionParams = parameters
    }

    override fun evaluate(
        currentPath: String,
        parent: PathRef,
        model: Any?,
        ctx: EvaluationContextImpl,
    ) {
        val pathFunction = PathFunctionFactory.newFunction(functionName)
        evaluateParameters(currentPath, parent, model, ctx)
        val result = pathFunction.invoke(currentPath, parent, model, ctx, functionParams)
        ctx.addResult(currentPath + "." + functionName, parent, result)
        cleanWildcardPathToken()
        if (!isLeaf()) next().evaluate(currentPath, parent, result, ctx)
    }

    private fun cleanWildcardPathToken() {
        val parameters = functionParams ?: return
        if (parameters.isEmpty()) return
        val path = parameters[0].getPath()
        if (path is CompiledPath && !path.isFunctionPath()) {
            var tail = path.getRoot().getNext()
            while (tail != null) {
                val following = tail.getNext() ?: break
                if (following is WildcardPathToken) {
                    tail.setNext(following.getNext())
                    break
                }
                tail = following
            }
        }
    }

    private fun evaluateParameters(
        currentPath: String,
        parent: PathRef,
        model: Any?,
        ctx: EvaluationContextImpl,
    ) {
        val parameters = functionParams
        if (parameters != null) {
            for (param in parameters) {
                when (checkNotNull(param.getType()) { "Parameter type must be configured before evaluation" }) {
                    ParamType.PATH -> {
                        val pathLateBindingValue =
                            PathLateBindingValue(
                                checkNotNull(param.getPath()) {
                                    "PATH parameter requires a path"
                                },
                                checkNotNull(ctx.rootDocument()) { "Evaluation requires a root document" },
                                ctx.configuration(),
                            )
                        if (!param.hasEvaluated() ||
                            pathLateBindingValue != param.getILateBingValue()
                        ) {
                            param.setLateBinding(pathLateBindingValue)
                            param.setEvaluated(true)
                        }
                    }

                    ParamType.JSON -> {
                        if (!param.hasEvaluated()) {
                            param.setLateBinding(JsonLateBindingValue(ctx.configuration().jsonProvider(), param))
                            param.setEvaluated(true)
                        }
                    }
                }
            }
        }
    }

    override fun isTokenDefinite(): Boolean = true

    override fun getPathFragment(): String = "." + pathFragment

    open fun setParameters(parameters: List<@JvmSuppressWildcards Parameter>?) {
        functionParams = parameters
    }

    open fun getParameters(): List<@JvmSuppressWildcards Parameter>? = functionParams

    open fun getFunctionName(): String? = functionName
}
