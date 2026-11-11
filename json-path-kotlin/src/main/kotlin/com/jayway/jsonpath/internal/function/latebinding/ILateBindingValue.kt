package com.jayway.jsonpath.internal.function.latebinding

fun interface ILateBindingValue {
    fun get(): Any?
}
