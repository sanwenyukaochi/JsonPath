package com.jayway.jsonpath

interface ReadContext {
    fun configuration(): Configuration

    fun <T> json(): T

    fun jsonString(): String

    fun <T> read(
        path: String,
        vararg filters: Predicate,
    ): T?

    fun <T> read(
        path: String,
        type: Class<T>,
        vararg filters: Predicate,
    ): T?

    fun <T> read(path: JsonPath): T?

    fun <T> read(
        path: JsonPath,
        type: Class<T>,
    ): T?

    fun <T> read(
        path: JsonPath,
        typeRef: TypeRef<T>,
    ): T?

    fun <T> read(
        path: String,
        typeRef: TypeRef<T>,
    ): T?

    fun limit(maxResults: Int): ReadContext

    fun withListeners(vararg listener: EvaluationListener): ReadContext
}
