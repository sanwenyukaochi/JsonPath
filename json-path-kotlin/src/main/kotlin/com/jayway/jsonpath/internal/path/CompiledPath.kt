package com.jayway.jsonpath.internal.path

import com.jayway.jsonpath.PathNotFoundException

internal class CompiledPath(
    private val properties: List<String>,
) : Path {
    override fun evaluate(rootDocument: Any): Any? {
        var current: Any? = rootDocument

        for (property in properties) {
            val objectNode =
                current as? Map<*, *>
                    ?: throw PathNotFoundException(
                        "Property '$property' can not be read from a non-object value",
                    )

            if (!objectNode.containsKey(property)) {
                throw PathNotFoundException("Missing property: $property")
            }

            current = objectNode[property]
        }

        return current
    }
}
