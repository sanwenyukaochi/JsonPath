package com.jayway.jsonpath.internal.filter

import com.jayway.jsonpath.InvalidPathException
import java.util.Locale

enum class RelationalOperator(
    private val operatorString: String,
) {
    GTE(">="),
    LTE("<="),
    EQ("=="),
    TSEQ("==="),
    NE("!="),
    TSNE("!=="),
    LT("<"),
    GT(">"),
    REGEX("=~"),
    NIN("NIN"),
    IN("IN"),
    CONTAINS("CONTAINS"),
    ALL("ALL"),
    SIZE("SIZE"),
    EXISTS("EXISTS"),
    TYPE("TYPE"),
    MATCHES("MATCHES"),
    EMPTY("EMPTY"),
    SUBSETOF("SUBSETOF"),
    ANYOF("ANYOF"),
    NONEOF("NONEOF"),
    ;

    override fun toString(): String = operatorString

    companion object {
        @JvmStatic
        fun fromString(operatorString: String): RelationalOperator {
            val upperCaseOperatorString = operatorString.uppercase(Locale.ROOT)
            for (operator in entries) {
                if (operator.operatorString == upperCaseOperatorString) {
                    return operator
                }
            }
            throw InvalidPathException("Filter operator $operatorString is not supported!")
        }
    }
}
