package com.jayway.jsonpath.internal.path

import com.jayway.jsonpath.InvalidPathException
import java.util.regex.Pattern

open class ArraySliceOperation private constructor(
    private val fromValue: Int?,
    private val toValue: Int?,
    private val operationValue: Operation,
) {
    enum class Operation {
        SLICE_FROM,
        SLICE_TO,
        SLICE_BETWEEN,
    }

    open fun from(): Int? = fromValue

    open fun to(): Int? = toValue

    open fun operation(): Operation = operationValue

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("[")
        stringBuilder.append(fromValue?.toString() ?: "")
        stringBuilder.append(":")
        stringBuilder.append(toValue?.toString() ?: "")
        stringBuilder.append("]")
        return stringBuilder.toString()
    }

    companion object {
        private val COLON = Pattern.compile(":")

        @JvmStatic
        fun parse(operation: String): ArraySliceOperation {
            for (i in operation.indices) {
                val character = operation[i]
                if (!Character.isDigit(character) && character != '-' && character != ':') {
                    throw InvalidPathException("Failed to parse SliceOperation: $operation")
                }
            }
            val tokens = COLON.split(operation)

            val tempFrom = tryRead(tokens, 0)
            val tempTo = tryRead(tokens, 1)
            val tempOperation: Operation

            if (tempFrom != null && tempTo == null) {
                tempOperation = Operation.SLICE_FROM
            } else if (tempFrom != null) {
                tempOperation = Operation.SLICE_BETWEEN
            } else if (tempTo != null) {
                tempOperation = Operation.SLICE_TO
            } else {
                throw InvalidPathException("Failed to parse SliceOperation: $operation")
            }

            return ArraySliceOperation(tempFrom, tempTo, tempOperation)
        }

        private fun tryRead(
            tokens: Array<String>,
            index: Int,
        ): Int? {
            if (tokens.size > index) {
                if (tokens[index] == "") {
                    return null
                }
                return Integer.parseInt(tokens[index])
            }
            return null
        }
    }
}
