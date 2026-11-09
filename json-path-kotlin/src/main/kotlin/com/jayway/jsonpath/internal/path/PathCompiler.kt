package com.jayway.jsonpath.internal.path

import com.jayway.jsonpath.InvalidPathException

internal object PathCompiler {
    fun compile(path: String): Path {
        if (path == "$") {
            return CompiledPath(emptyList())
        }

        if (!path.startsWith("$.") || path.length == 2) {
            throw InvalidPathException("Invalid path: $path")
        }

        val properties = path.substring(2).split('.')
        if (properties.any(String::isEmpty)) {
            throw InvalidPathException("Invalid path: $path")
        }

        return CompiledPath(properties)
    }
}
