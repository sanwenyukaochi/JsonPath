package com.jayway.jsonpath

interface WriteContext {
    fun configuration(): Configuration

    fun <T> json(): T

    fun jsonString(): String

    fun set(
        path: String,
        newValue: Any?,
        vararg filters: Predicate,
    ): DocumentContext

    fun set(
        path: JsonPath,
        newValue: Any?,
    ): DocumentContext

    fun map(
        path: String,
        mapFunction: MapFunction,
        vararg filters: Predicate,
    ): DocumentContext

    fun map(
        path: JsonPath,
        mapFunction: MapFunction,
    ): DocumentContext?

    fun delete(
        path: String,
        vararg filters: Predicate,
    ): DocumentContext

    fun delete(path: JsonPath): DocumentContext

    fun add(
        path: String,
        value: Any?,
        vararg filters: Predicate,
    ): DocumentContext

    fun add(
        path: JsonPath,
        value: Any?,
    ): DocumentContext

    fun put(
        path: String,
        key: String,
        value: Any?,
        vararg filters: Predicate,
    ): DocumentContext

    fun put(
        path: JsonPath,
        key: String,
        value: Any?,
    ): DocumentContext

    fun renameKey(
        path: String,
        oldKeyName: String,
        newKeyName: String,
        vararg filters: Predicate,
    ): DocumentContext

    fun renameKey(
        path: JsonPath,
        oldKeyName: String,
        newKeyName: String,
    ): DocumentContext
}
