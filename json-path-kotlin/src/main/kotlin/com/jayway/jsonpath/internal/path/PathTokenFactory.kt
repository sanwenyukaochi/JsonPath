package com.jayway.jsonpath.internal.path
import com.jayway.jsonpath.Predicate
import com.jayway.jsonpath.internal.function.Parameter

open class PathTokenFactory {
    companion object {
        @JvmStatic fun createRootPathToken(token: Char): RootPathToken = RootPathToken(token)

        @JvmStatic fun createSinglePropertyPathToken(
            property: String,
            stringDelimiter: Char,
        ): PathToken = PropertyPathToken(java.util.Collections.singletonList(property), stringDelimiter)

        @JvmStatic fun createPropertyPathToken(
            properties: List<@JvmSuppressWildcards String>,
            stringDelimiter: Char,
        ): PathToken = PropertyPathToken(properties, stringDelimiter)

        @JvmStatic fun createSliceArrayPathToken(arraySliceOperation: ArraySliceOperation): PathToken = ArraySliceToken(arraySliceOperation)

        @JvmStatic fun createIndexArrayPathToken(arrayIndexOperation: ArrayIndexOperation): PathToken = ArrayIndexToken(arrayIndexOperation)

        @JvmStatic fun createWildCardPathToken(): PathToken = WildcardPathToken()

        @JvmStatic fun crateScanToken(): PathToken = ScanPathToken()

        @JvmStatic fun createPredicatePathToken(predicates: Collection<@JvmSuppressWildcards Predicate>): PathToken =
            PredicatePathToken(
                predicates,
            )

        @JvmStatic fun createPredicatePathToken(predicate: Predicate): PathToken = PredicatePathToken(predicate)

        @JvmStatic fun createFunctionPathToken(
            function: String,
            parameters: List<@JvmSuppressWildcards Parameter>?,
        ): PathToken = FunctionPathToken(function, parameters)
    }
}
