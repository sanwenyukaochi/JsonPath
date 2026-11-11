package com.jayway.jsonpath.internal.function
import com.jayway.jsonpath.internal.EvaluationContext
import com.jayway.jsonpath.internal.Path
import com.jayway.jsonpath.internal.function.latebinding.ILateBindingValue

open class Parameter {
    private var type: ParamType? = null
    private var path: Path? = null
    private var lateBinding: ILateBindingValue? = null
    private var evaluated: Boolean = false
    private var json: String? = null
    constructor()
    constructor(json: String) {
        this.json = json
        type = ParamType.JSON
    }
    constructor(path: Path) {
        this.path = path
        type = ParamType.PATH
    }

    open fun getValue(): Any? = checkNotNull(lateBinding) { "Parameter must be bound before its value is read" }.get()

    open fun setLateBinding(lateBinding: ILateBindingValue) {
        this.lateBinding = lateBinding
    }

    open fun getPath(): Path? = path

    open fun setEvaluated(evaluated: Boolean?) {
        this.evaluated = requireNotNull(evaluated) { "evaluated cannot be null" }
    }

    open fun hasEvaluated(): Boolean = evaluated

    open fun getType(): ParamType? = type

    open fun setType(type: ParamType) {
        this.type = type
    }

    open fun setPath(path: Path) {
        this.path = path
    }

    open fun getJson(): String? = json

    open fun setJson(json: String) {
        this.json = json
    }

    open fun getILateBingValue(): ILateBindingValue? = lateBinding

    companion object {
        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <T> toList(
            type: Class<T>,
            ctx: EvaluationContext,
            parameters: List<@JvmSuppressWildcards Parameter>?,
        ): List<T> {
            val values = ArrayList<Any?>()
            if (parameters != null) for (param in parameters) consume(type, ctx, values, param.getValue())
            return values as List<T>
        }

        @JvmStatic fun consume(
            expectedType: Class<*>,
            ctx: EvaluationContext,
            collection: MutableCollection<Any?>,
            value: Any?,
        ) {
            if (ctx.configuration().jsonProvider().isArray(value)) {
                for (o in ctx.configuration().jsonProvider().toIterable(value)) {
                    if (o != null && expectedType.isAssignableFrom(o.javaClass)) {
                        collection.add(o)
                    } else if (o != null && expectedType == String::class.java) {
                        collection.add(o.toString())
                    }
                }
            } else {
                if (value != null && expectedType.isAssignableFrom(value.javaClass)) {
                    collection.add(value)
                } else if (value != null && expectedType == String::class.java) {
                    collection.add(value.toString())
                }
            }
        }
    }
}
