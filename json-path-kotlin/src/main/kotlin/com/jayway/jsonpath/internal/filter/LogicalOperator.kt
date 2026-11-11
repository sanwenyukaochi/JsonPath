package com.jayway.jsonpath.internal.filter

import com.jayway.jsonpath.InvalidPathException

enum class LogicalOperator(
    private val value: String,
) {
    AND("&&"),
    NOT("!"),
    OR("||"),
    ;

    fun getOperatorString(): String = value

    override fun toString(): String = value

    companion object {
        @JvmStatic
        fun fromString(operatorString: String?): LogicalOperator {
            if (AND.value == operatorString) {
                return AND
            } else if (NOT.value == operatorString) {
                return NOT
            } else if (OR.value == operatorString) {
                return OR
            }
            throw InvalidPathException("Failed to parse operator $operatorString")
        }
    }
}
