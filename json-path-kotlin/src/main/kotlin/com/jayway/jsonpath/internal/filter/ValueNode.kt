package com.jayway.jsonpath.internal.filter
import com.jayway.jsonpath.InvalidPathException
import com.jayway.jsonpath.JsonPathException
import com.jayway.jsonpath.Predicate
import com.jayway.jsonpath.internal.Path
import com.jayway.jsonpath.internal.path.PathCompiler
import net.minidev.json.parser.JSONParser
import java.time.OffsetDateTime
import java.util.regex.Pattern

abstract class ValueNode {
    abstract fun type(ctx: Predicate.PredicateContext): Class<*>

    open fun isPatternNode(): Boolean = false

    open fun asPatternNode(): ValueNodes.PatternNode = throw InvalidPathException("Expected regexp node")

    open fun isPathNode(): Boolean = false

    open fun asPathNode(): ValueNodes.PathNode = throw InvalidPathException("Expected path node")

    open fun isNumberNode(): Boolean = false

    open fun asNumberNode(): ValueNodes.NumberNode = throw InvalidPathException("Expected number node")

    open fun isStringNode(): Boolean = false

    open fun asStringNode(): ValueNodes.StringNode = throw InvalidPathException("Expected string node")

    open fun isBooleanNode(): Boolean = false

    open fun asBooleanNode(): ValueNodes.BooleanNode = throw InvalidPathException("Expected boolean node")

    open fun isJsonNode(): Boolean = false

    open fun asJsonNode(): ValueNodes.JsonNode = throw InvalidPathException("Expected json node")

    open fun isPredicateNode(): Boolean = false

    open fun asPredicateNode(): ValueNodes.PredicateNode = throw InvalidPathException("Expected predicate node")

    open fun isValueListNode(): Boolean = false

    open fun asValueListNode(): ValueNodes.ValueListNode = throw InvalidPathException("Expected value list node")

    open fun isNullNode(): Boolean = false

    open fun asNullNode(): ValueNodes.NullNode = throw InvalidPathException("Expected null node")

    open fun isUndefinedNode(): Boolean = false

    open fun asUndefinedNode(): ValueNodes.UndefinedNode = throw InvalidPathException("Expected undefined node")

    open fun isClassNode(): Boolean = false

    open fun asClassNode(): ValueNodes.ClassNode = throw InvalidPathException("Expected class node")

    open fun isOffsetDateTimeNode(): Boolean = false

    open fun asOffsetDateTimeNode(): ValueNodes.OffsetDateTimeNode = throw InvalidPathException("Expected offsetDateTime node")

    companion object {
        @JvmStatic private fun isPath(o: Any?): Boolean {
            if (o !is String) return false
            val str = o.trim { it <= ' ' }
            if (str.length <= 0) return false
            val c0 = str[0]
            if (c0 == '@' || c0 == '$') {
                try {
                    PathCompiler.compile(str)
                    return true
                } catch (_: Exception) {
                    return false
                }
            }
            return false
        }

        @JvmStatic private fun isJson(o: Any?): Boolean {
            if (o !is String) return false
            val str = o.trim { it <= ' ' }
            if (str.length <= 1) return false
            val c0 = str[0]
            val c1 = str[str.length - 1]
            if ((c0 == '[' && c1 == ']') || (c0 == '{' && c1 == '}')) {
                try {
                    JSONParser(JSONParser.MODE_PERMISSIVE).parse(str)
                    return true
                } catch (_: Exception) {
                    return false
                }
            }
            return false
        }

        @JvmStatic fun toValueNode(o: Any?): ValueNode {
            if (o == null) return ValueNodes.NULL_NODE
            if (o is ValueNode) return o
            if (o is Class<*>) {
                return createClassNode(o)
            } else if (isPath(o)) {
                return ValueNodes.PathNode(o.toString(), false, false)
            } else if (isJson(o)) {
                return createJsonNode(o.toString() as CharSequence)
            } else if (o is String) {
                return createStringNode(o, true)
            } else if (o is Char) {
                return createStringNode(o.toString(), false)
            } else if (o is Number) {
                return createNumberNode(o.toString())
            } else if (o is Boolean) {
                return createBooleanNode(o.toString())
            } else if (o is Pattern) {
                return createPatternNode(o)
            } else if (o is OffsetDateTime) {
                return createOffsetDateTimeNode(o.toString())
            } else {
                throw JsonPathException("Could not determine value type")
            }
        }

        @JvmStatic fun createStringNode(
            charSequence: CharSequence,
            escape: Boolean,
        ): ValueNodes.StringNode = ValueNodes.StringNode(charSequence, escape)

        @JvmStatic fun createClassNode(clazz: Class<*>): ValueNodes.ClassNode = ValueNodes.ClassNode(clazz)

        @JvmStatic fun createNumberNode(charSequence: CharSequence): ValueNodes.NumberNode = ValueNodes.NumberNode(charSequence)

        @JvmStatic fun createBooleanNode(charSequence: CharSequence): ValueNodes.BooleanNode =
            if (java.lang.Boolean.parseBoolean(charSequence.toString())) ValueNodes.TRUE else ValueNodes.FALSE

        @JvmStatic fun createNullNode(): ValueNodes.NullNode = ValueNodes.NULL_NODE

        @JvmStatic fun createJsonNode(json: CharSequence): ValueNodes.JsonNode = ValueNodes.JsonNode(json)

        @JvmStatic fun createJsonNode(parsedJson: Any?): ValueNodes.JsonNode = ValueNodes.JsonNode(parsedJson)

        @JvmStatic fun createPatternNode(pattern: CharSequence): ValueNodes.PatternNode = ValueNodes.PatternNode(pattern)

        @JvmStatic fun createPatternNode(pattern: Pattern): ValueNodes.PatternNode = ValueNodes.PatternNode(pattern)

        @JvmStatic fun createOffsetDateTimeNode(charSequence: CharSequence): ValueNodes.OffsetDateTimeNode =
            ValueNodes.OffsetDateTimeNode(charSequence)

        @JvmStatic fun createUndefinedNode(): ValueNodes.UndefinedNode = ValueNodes.UNDEFINED

        @JvmStatic fun createPathNode(
            path: CharSequence,
            existsCheck: Boolean,
            shouldExists: Boolean,
        ): ValueNodes.PathNode = ValueNodes.PathNode(path, existsCheck, shouldExists)

        @JvmStatic fun createPathNode(path: Path): ValueNode = ValueNodes.PathNode(path)
    }
}
