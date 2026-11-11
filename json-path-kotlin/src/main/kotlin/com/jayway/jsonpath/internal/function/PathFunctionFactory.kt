package com.jayway.jsonpath.internal.function
import com.jayway.jsonpath.InvalidPathException
import com.jayway.jsonpath.internal.function.json.Append
import com.jayway.jsonpath.internal.function.json.KeySetFunction
import com.jayway.jsonpath.internal.function.numeric.Average
import com.jayway.jsonpath.internal.function.numeric.Max
import com.jayway.jsonpath.internal.function.numeric.Min
import com.jayway.jsonpath.internal.function.numeric.StandardDeviation
import com.jayway.jsonpath.internal.function.numeric.Sum
import com.jayway.jsonpath.internal.function.sequence.First
import com.jayway.jsonpath.internal.function.sequence.Index
import com.jayway.jsonpath.internal.function.sequence.Last
import com.jayway.jsonpath.internal.function.text.Concatenate
import com.jayway.jsonpath.internal.function.text.Length
import java.util.Collections

open class PathFunctionFactory {
    companion object {
        @JvmField val FUNCTIONS: Map<String, Class<*>> =
            Collections.unmodifiableMap(
                hashMapOf(
                    "avg" to Average::class.java,
                    "stddev" to StandardDeviation::class.java,
                    "sum" to Sum::class.java,
                    "min" to Min::class.java,
                    "max" to Max::class.java,
                    "concat" to Concatenate::class.java,
                    Length.TOKEN_NAME to Length::class.java,
                    "size" to Length::class.java,
                    "append" to Append::class.java,
                    "keys" to KeySetFunction::class.java,
                    "first" to First::class.java,
                    "last" to Last::class.java,
                    "index" to Index::class.java,
                ),
            )

        @JvmStatic
        @Throws(InvalidPathException::class)
        @Suppress("DEPRECATION")
        fun newFunction(name: String): PathFunction {
            val functionClazz = FUNCTIONS[name]
            if (functionClazz == null) {
                throw InvalidPathException("Function with name: " + name + " does not exist.")
            } else {
                try {
                    return functionClazz.newInstance() as PathFunction
                } catch (
                    e: Exception,
                ) {
                    throw InvalidPathException("Function of name: " + name + " cannot be created", e)
                }
            }
        }
    }
}
