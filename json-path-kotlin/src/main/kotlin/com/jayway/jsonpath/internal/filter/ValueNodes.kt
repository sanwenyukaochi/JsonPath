package com.jayway.jsonpath.internal.filter
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPathException
import com.jayway.jsonpath.Option
import com.jayway.jsonpath.PathNotFoundException
import com.jayway.jsonpath.Predicate
import com.jayway.jsonpath.internal.Path
import com.jayway.jsonpath.internal.Utils
import com.jayway.jsonpath.internal.path.PathCompiler
import com.jayway.jsonpath.internal.path.PredicateContextImpl
import com.jayway.jsonpath.spi.json.JsonProvider
import net.minidev.json.parser.JSONParser
import net.minidev.json.parser.ParseException
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.Collections
import java.util.regex.Pattern

interface ValueNodes {
    open class PatternNode : ValueNode {
        private val pattern: String
        private val compiledPattern: Pattern
        private val flags: String
        constructor(charSequence: CharSequence) {
            val tmp = charSequence.toString()
            val begin = tmp.indexOf('/')
            val end = tmp.lastIndexOf('/')
            pattern = tmp.substring(begin + 1, end)
            val flagsIndex = end + 1
            flags = if (tmp.length > flagsIndex) tmp.substring(flagsIndex) else ""
            compiledPattern = Pattern.compile(pattern, PatternFlag.parseFlags(flags.toCharArray()))
        }
        constructor(pattern: Pattern) {
            this.pattern = pattern.pattern()
            compiledPattern = pattern
            flags = PatternFlag.parseFlags(pattern.flags())
        }

        open fun getCompiledPattern(): Pattern = compiledPattern

        override fun type(ctx: Predicate.PredicateContext): Class<*> = Void.TYPE

        override fun isPatternNode(): Boolean = true

        override fun asPatternNode(): PatternNode = this

        override fun toString(): String = if (!pattern.startsWith("/")) "/" + pattern + "/" + flags else pattern

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is PatternNode) return false
            return compiledPattern == other.compiledPattern
        }
    }

    open class JsonNode : ValueNode {
        private val json: Any?
        private val parsed: Boolean
        constructor(charSequence: CharSequence) {
            json = charSequence.toString()
            parsed = false
        }
        constructor(parsedJson: Any?) {
            json = parsedJson
            parsed = true
        }

        override fun type(ctx: Predicate.PredicateContext): Class<*> {
            if (isArray(ctx)) {
                return List::class.java
            } else if (isMap(ctx)) {
                return Map::class.java
            } else if (parse(ctx) is Number) {
                return Number::class.java
            } else if (parse(ctx) is String) {
                return String::class.java
            } else if (parse(ctx) is Boolean) {
                return Boolean::class.javaObjectType
            } else {
                return Void::class.java
            }
        }

        override fun isJsonNode(): Boolean = true

        override fun asJsonNode(): JsonNode = this

        open fun asValueListNode(ctx: Predicate.PredicateContext): ValueNode {
            if (!isArray(ctx)) {
                return UNDEFINED
            } else {
                return ValueListNode(Collections.unmodifiableList(parse(ctx) as List<*>))
            }
        }

        open fun parse(ctx: Predicate.PredicateContext): Any? {
            try {
                return if (parsed) {
                    json
                } else {
                    JSONParser(JSONParser.MODE_PERMISSIVE).parse(
                        checkNotNull(json) {
                            "Unparsed JSON requires JSON text"
                        }.toString(),
                    )
                }
            } catch (
                e: ParseException,
            ) {
                throw IllegalArgumentException(e)
            }
        }

        open fun isParsed(): Boolean = parsed

        open fun getJson(): Any? = json

        open fun isArray(ctx: Predicate.PredicateContext): Boolean = parse(ctx) is List<*>

        open fun isMap(ctx: Predicate.PredicateContext): Boolean = parse(ctx) is Map<*, *>

        open fun length(ctx: Predicate.PredicateContext): Int = if (isArray(ctx)) (parse(ctx) as List<*>).size else -1

        open fun isEmpty(ctx: Predicate.PredicateContext): Boolean {
            if (isArray(ctx) || isMap(ctx)) {
                return (parse(ctx) as Collection<*>).size == 0
            } else if (parse(ctx) is String) {
                return (parse(ctx) as String).length == 0
            }
            return true
        }

        override fun toString(): String = json.toString()

        open fun equals(
            jsonNode: JsonNode,
            ctx: Predicate.PredicateContext,
        ): Boolean {
            if (this === jsonNode) return true
            return if (json != null) json == jsonNode.parse(ctx) else jsonNode.json == null
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is JsonNode) return false
            return json == other.json
        }
    }

    open class StringNode(
        charSequence: CharSequence,
        escape: Boolean,
    ) : ValueNode() {
        private val string: String
        private var useSingleQuote = true

        init {
            var chars = charSequence
            if (escape && chars.length > 1) {
                val open = chars[0]
                val close = chars[chars.length - 1]
                if (open == '\'' && close == '\'') {
                    chars = chars.subSequence(1, chars.length - 1)
                } else if (open == '"' && close == '"') {
                    chars = chars.subSequence(1, chars.length - 1)
                    useSingleQuote = false
                }
                string = checkNotNull(Utils.unescape(chars.toString())) { "Unescape must preserve non-null text" }
            } else {
                string = chars.toString()
            }
        }

        override fun asNumberNode(): NumberNode {
            val number: BigDecimal
            try {
                number = BigDecimal(string)
            } catch (_: NumberFormatException) {
                return NumberNode.NAN
            }
            return NumberNode(number)
        }

        open fun getString(): String = string

        open fun length(): Int = getString().length

        open fun isEmpty(): Boolean = getString().isEmpty()

        open fun contains(str: String): Boolean = getString().contains(str)

        override fun type(ctx: Predicate.PredicateContext): Class<*> = String::class.java

        override fun isStringNode(): Boolean = true

        override fun asStringNode(): StringNode = this

        override fun toString(): String {
            val quote = if (useSingleQuote) "'" else "\""
            return quote + Utils.escape(string, true) + quote
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is StringNode && other !is NumberNode) return false
            val that = other.asStringNode()
            return string == that.getString()
        }
    }

    open class NumberNode : ValueNode {
        private val number: BigDecimal?
        constructor(number: BigDecimal?) {
            this.number = number
        }
        constructor(num: CharSequence) {
            number = BigDecimal(num.toString())
        }

        override fun asStringNode(): StringNode = StringNode((number?.toString() ?: "NaN"), false)

        open fun getNumber(): BigDecimal? = number

        override fun type(ctx: Predicate.PredicateContext): Class<*> = Number::class.java

        override fun isNumberNode(): Boolean = true

        override fun asNumberNode(): NumberNode = this

        override fun toString(): String = (number?.toString() ?: "NaN")

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is NumberNode && other !is StringNode) return false
            val that = other.asNumberNode()
            val leftNumber = number ?: return false
            val rightNumber = that.number ?: return false
            return if (that === NAN) false else leftNumber.compareTo(rightNumber) == 0
        }

        companion object {
            @Suppress("ktlint:standard:property-naming")
            @JvmField
            var NAN: NumberNode = NumberNode(null as BigDecimal?)
        }
    }

    open class OffsetDateTimeNode : ValueNode {
        private val dateTime: OffsetDateTime
        constructor(dateTime: OffsetDateTime) {
            this.dateTime = dateTime
        }
        constructor(date: CharSequence) {
            dateTime = OffsetDateTime.parse(date)
        }

        override fun asStringNode(): StringNode = StringNode(dateTime.toString(), false)

        open fun getDate(): OffsetDateTime = dateTime

        override fun type(ctx: Predicate.PredicateContext): Class<*> = OffsetDateTimeNode::class.java

        override fun isOffsetDateTimeNode(): Boolean = true

        override fun asOffsetDateTimeNode(): OffsetDateTimeNode = this

        override fun toString(): String = dateTime.toString()

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is OffsetDateTimeNode && other !is StringNode) return false
            val that = other.asOffsetDateTimeNode()
            return dateTime.compareTo(that.dateTime) == 0
        }
    }

    open class BooleanNode private constructor(
        boolValue: CharSequence,
    ) : ValueNode() {
        private val value = java.lang.Boolean.parseBoolean(boolValue.toString())

        override fun type(ctx: Predicate.PredicateContext): Class<*> = Boolean::class.javaObjectType

        override fun isBooleanNode(): Boolean = true

        override fun asBooleanNode(): BooleanNode = this

        open fun getBoolean(): Boolean = value

        override fun toString(): String = value.toString()

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is BooleanNode) return false
            return value == other.value
        }

        companion object {
            internal fun create(value: String): BooleanNode = BooleanNode(value)
        }
    }

    open class ClassNode(
        private val clazz: Class<*>,
    ) : ValueNode() {
        override fun type(ctx: Predicate.PredicateContext): Class<*> = Class::class.java

        override fun isClassNode(): Boolean = true

        override fun asClassNode(): ClassNode = this

        open fun getClazz(): Class<*> = clazz

        override fun toString(): String = clazz.name

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ClassNode) return false
            return clazz == other.clazz
        }
    }

    open class NullNode private constructor() : ValueNode() {
        override fun type(ctx: Predicate.PredicateContext): Class<*> = Void::class.java

        override fun isNullNode(): Boolean = true

        override fun asNullNode(): NullNode = this

        override fun toString(): String = "null"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is NullNode) return false
            return true
        }

        companion object {
            internal fun create(): NullNode = NullNode()
        }
    }

    open class UndefinedNode : ValueNode() {
        override fun type(ctx: Predicate.PredicateContext): Class<*> = Void::class.java

        override fun asUndefinedNode(): UndefinedNode = this

        override fun isUndefinedNode(): Boolean = true

        override fun equals(other: Any?): Boolean = false
    }

    open class PredicateNode(
        private val predicate: Predicate,
    ) : ValueNode() {
        open fun getPredicate(): Predicate = predicate

        override fun asPredicateNode(): PredicateNode = this

        override fun type(ctx: Predicate.PredicateContext): Class<*> = Void::class.java

        override fun isPredicateNode(): Boolean = true

        override fun equals(other: Any?): Boolean = false

        override fun toString(): String = predicate.toString()
    }

    open class ValueListNode(
        values: Collection<*>,
    ) : ValueNode(),
        Iterable<ValueNode> {
        private var nodes: MutableList<ValueNode> = ArrayList()

        init {
            for (value in values) nodes.add(toValueNode(value))
        }

        open fun contains(node: ValueNode?): Boolean = nodes.contains(node)

        open fun subsetof(right: ValueListNode): Boolean {
            for (leftNode in nodes) if (!right.nodes.contains(leftNode)) return false
            return true
        }

        open fun getNodes(): List<ValueNode> = Collections.unmodifiableList(nodes)

        override fun type(ctx: Predicate.PredicateContext): Class<*> = List::class.java

        override fun isValueListNode(): Boolean = true

        override fun asValueListNode(): ValueListNode = this

        override fun toString(): String = "[" + Utils.join(",", nodes) + "]"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ValueListNode) return false
            return nodes == other.nodes
        }

        override fun iterator(): MutableIterator<ValueNode> = nodes.iterator()
    }

    open class PathNode(
        private val path: Path,
        private val existsCheck: Boolean,
        private val shouldExist: Boolean,
    ) : ValueNode() {
        constructor(path: Path) : this(path, false, false)
        constructor(
            charSequence: CharSequence,
            existsCheck: Boolean,
            shouldExist: Boolean,
        ) : this(PathCompiler.compile(charSequence.toString()), existsCheck, shouldExist)

        init {
            logger.trace("PathNode {} existsCheck: {}", path, existsCheck)
        }

        open fun getPath(): Path = path

        open fun isExistsCheck(): Boolean = existsCheck

        open fun shouldExists(): Boolean = shouldExist

        override fun type(ctx: Predicate.PredicateContext): Class<*> = Void::class.java

        override fun isPathNode(): Boolean = true

        override fun asPathNode(): PathNode = this

        open fun asExistsCheck(shouldExist: Boolean): PathNode = PathNode(path, true, shouldExist)

        override fun toString(): String = if (existsCheck && !shouldExist) Utils.concat("!", path.toString()) else path.toString()

        open fun evaluate(ctx: Predicate.PredicateContext): ValueNode {
            if (isExistsCheck()) {
                try {
                    val c =
                        Configuration
                            .builder()
                            .jsonProvider(
                                ctx.configuration().jsonProvider(),
                            ).options(Option.REQUIRE_PROPERTIES)
                            .build()
                    val result: Any? = path.evaluate(ctx.item(), ctx.root(), c).getValue(false)
                    return if (result === JsonProvider.UNDEFINED) FALSE else TRUE
                } catch (_: PathNotFoundException) {
                    return FALSE
                }
            } else {
                try {
                    var res: Any?
                    if (ctx is PredicateContextImpl) {
                        res = ctx.evaluate(path)
                    } else {
                        val doc = if (path.isRootPath()) ctx.root() else ctx.item()
                        res = path.evaluate(doc, ctx.root(), ctx.configuration()).getValue()
                    }
                    res = ctx.configuration().jsonProvider().unwrap(res)
                    if (res is Number) {
                        return createNumberNode(res.toString())
                    } else if (res is String) {
                        return createStringNode(res, false)
                    } else if (res is Boolean) {
                        return createBooleanNode(res.toString())
                    } else if (res is OffsetDateTime) {
                        return createOffsetDateTimeNode(res.toString())
                    } else if (res == null) {
                        return NULL_NODE
                    } else if (ctx.configuration().jsonProvider().isArray(res)) {
                        return createJsonNode(ctx.configuration().mappingProvider().map(res, List::class.java, ctx.configuration()) as Any?)
                    } else if (ctx.configuration().jsonProvider().isMap(res)) {
                        return createJsonNode(ctx.configuration().mappingProvider().map(res, Map::class.java, ctx.configuration()) as Any?)
                    } else {
                        throw JsonPathException("Could not convert " + res.javaClass.toString() + ":" + res.toString() + " to a ValueNode")
                    }
                } catch (_: PathNotFoundException) {
                    return UNDEFINED
                }
            }
        }

        companion object {
            private val logger = LoggerFactory.getLogger(PathNode::class.java)
        }
    }

    companion object {
        @JvmField val NULL_NODE: NullNode = NullNode.create()

        @JvmField val TRUE: BooleanNode = BooleanNode.create("true")

        @JvmField val FALSE: BooleanNode = BooleanNode.create("false")

        @JvmField val UNDEFINED: UndefinedNode = UndefinedNode()
    }
}
