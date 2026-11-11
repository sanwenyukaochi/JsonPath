package com.jayway.jsonpath.internal.function.latebinding
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.internal.Path
import java.util.Objects

open class PathLateBindingValue(
    private val path: Path,
    rootDocument: Any,
    private val configuration: Configuration,
) : ILateBindingValue {
    private val rootDocument: String = rootDocument.toString()
    private val result: Any? = path.evaluate(rootDocument, rootDocument, configuration).getValue()

    override fun get(): Any? = result

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        other as PathLateBindingValue
        return Objects.equals(path, other.path) && rootDocument == other.rootDocument && Objects.equals(configuration, other.configuration)
    }
}
