package com.jayway.jsonpath

import com.jayway.jsonpath.spi.mapper.MappingException

fun interface Predicate {
    fun apply(context: PredicateContext): Boolean

    interface PredicateContext {
        fun item(): Any?

        @Throws(MappingException::class)
        fun <T> item(clazz: Class<T>): T?

        fun root(): Any?

        fun configuration(): Configuration
    }
}
