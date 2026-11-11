package com.jayway.jsonpath

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

abstract class TypeRef<T> protected constructor() : Comparable<TypeRef<T>?> {
    @JvmField
    protected val type: Type

    init {
        val superClass = javaClass.genericSuperclass
        if (superClass is Class<*>) {
            throw IllegalArgumentException("No type info in TypeRef")
        }
        type = (superClass as ParameterizedType).actualTypeArguments[0]
    }

    open fun getType(): Type = type

    override fun compareTo(other: TypeRef<T>?): Int = 0
}
