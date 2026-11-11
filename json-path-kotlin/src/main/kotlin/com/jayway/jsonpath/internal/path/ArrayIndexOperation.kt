package com.jayway.jsonpath.internal.path

import com.jayway.jsonpath.InvalidPathException
import com.jayway.jsonpath.internal.Utils
import java.util.Collections
import java.util.regex.Pattern

open class ArrayIndexOperation private constructor(
    indexes: List<Int>,
) {
    private val indexesValue: List<Int> = Collections.unmodifiableList(indexes)

    open fun indexes(): List<Int> = indexesValue

    open fun isSingleIndexOperation(): Boolean = indexesValue.size == 1

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("[")
        stringBuilder.append(Utils.join(",", indexesValue))
        stringBuilder.append("]")
        return stringBuilder.toString()
    }

    companion object {
        private val COMMA = Pattern.compile("\\s*,\\s*")

        @JvmStatic
        fun parse(operation: String): ArrayIndexOperation {
            for (i in operation.indices) {
                val character = operation[i]
                if (!Character.isDigit(character) &&
                    character != ',' &&
                    character != ' ' &&
                    character != '-'
                ) {
                    throw InvalidPathException("Failed to parse ArrayIndexOperation: $operation")
                }
            }
            val tokens = COMMA.split(operation, -1)

            val tempIndexes = ArrayList<Int>(tokens.size)
            for (token in tokens) {
                tempIndexes.add(parseInteger(token))
            }

            return ArrayIndexOperation(tempIndexes)
        }

        private fun parseInteger(token: String): Int =
            try {
                Integer.parseInt(token)
            } catch (exception: Exception) {
                throw InvalidPathException(
                    "Failed to parse token in ArrayIndexOperation: $token",
                    exception,
                )
            }
    }
}
