package com.jayway.jsonpath.internal.filter
import com.jayway.jsonpath.Predicate

interface Evaluator {
    fun evaluate(
        left: ValueNode,
        right: ValueNode,
        ctx: Predicate.PredicateContext,
    ): Boolean
}
