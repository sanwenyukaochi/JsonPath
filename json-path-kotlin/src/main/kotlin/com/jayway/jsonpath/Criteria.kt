package com.jayway.jsonpath
import com.jayway.jsonpath.internal.Path
import com.jayway.jsonpath.internal.Utils
import com.jayway.jsonpath.internal.filter.RelationalExpressionNode
import com.jayway.jsonpath.internal.filter.RelationalOperator
import com.jayway.jsonpath.internal.filter.ValueNode
import com.jayway.jsonpath.internal.filter.ValueNodes
import java.util.LinkedList
import java.util.regex.Pattern

open class Criteria private constructor(
    private val criteriaChain: MutableList<Criteria>,
    private var left: ValueNode,
) : Predicate {
    private var criteriaType: RelationalOperator? = null
    private var right: ValueNode? = null

    init {
        criteriaChain.add(this)
    }
    private constructor(left: ValueNode) : this(LinkedList(), left)

    override fun apply(context: Predicate.PredicateContext): Boolean {
        for (expressionNode in toRelationalExpressionNodes()) if (!expressionNode.apply(context)) return false
        return true
    }

    override fun toString(): String = Utils.join(" && ", toRelationalExpressionNodes())

    private fun toRelationalExpressionNodes(): Collection<RelationalExpressionNode> {
        val nodes = ArrayList<RelationalExpressionNode>(criteriaChain.size)
        for (criteria in criteriaChain) {
            nodes.add(
                RelationalExpressionNode(
                    criteria.left,
                    checkNotNull(criteria.criteriaType) {
                        "Criteria operator has not been configured"
                    },
                    checkNotNull(criteria.right) { "Criteria value has not been configured" },
                ),
            )
        }
        return nodes
    }

    open fun and(key: String): Criteria {
        checkComplete()
        return Criteria(criteriaChain, ValueNode.toValueNode(prefixPath(key)))
    }

    open fun `is`(o: Any?): Criteria {
        criteriaType = RelationalOperator.EQ
        right = ValueNode.toValueNode(o)
        return this
    }

    open fun eq(o: Any?): Criteria = `is`(o)

    open fun regex(pattern: Pattern): Criteria {
        Utils.notNull(pattern, "pattern can not be null")
        criteriaType = RelationalOperator.REGEX
        right =
            ValueNode.toValueNode(pattern)
        return this
    }

    open fun contains(o: Any?): Criteria {
        criteriaType = RelationalOperator.CONTAINS
        right = ValueNode.toValueNode(o)
        return this
    }

    open fun size(size: Int): Criteria {
        criteriaType = RelationalOperator.SIZE
        right = ValueNode.toValueNode(size)
        return this
    }

    open fun type(clazz: Class<*>): Criteria {
        criteriaType = RelationalOperator.TYPE
        right = ValueNode.createClassNode(clazz)
        return this
    }

    open fun exists(shouldExist: Boolean): Criteria {
        criteriaType = RelationalOperator.EXISTS
        right = ValueNode.toValueNode(shouldExist)
        left =
            left.asPathNode().asExistsCheck(shouldExist)
        return this
    }

    @Deprecated("Use empty(false)")
    open fun notEmpty(): Criteria = empty(false)

    open fun empty(empty: Boolean): Criteria {
        criteriaType = RelationalOperator.EMPTY
        right =
            if (empty) ValueNodes.TRUE else ValueNodes.FALSE
        return this
    }

    open fun matches(p: Predicate): Criteria {
        criteriaType = RelationalOperator.MATCHES
        right = ValueNodes.PredicateNode(p)
        return this
    }

    open fun ne(o: Any?): Criteria {
        criteriaType = RelationalOperator.NE
        right = ValueNode.toValueNode(o)
        return this
    }

    open fun lt(o: Any?): Criteria {
        criteriaType = RelationalOperator.LT
        right = ValueNode.toValueNode(o)
        return this
    }

    open fun lte(o: Any?): Criteria {
        criteriaType = RelationalOperator.LTE
        right = ValueNode.toValueNode(o)
        return this
    }

    open fun gt(o: Any?): Criteria {
        criteriaType = RelationalOperator.GT
        right = ValueNode.toValueNode(o)
        return this
    }

    open fun gte(o: Any?): Criteria {
        criteriaType = RelationalOperator.GTE
        right = ValueNode.toValueNode(o)
        return this
    }

    open fun `in`(vararg o: Any?): Criteria = `in`(java.util.Arrays.asList(*o))

    open fun `in`(c: Collection<*>): Criteria {
        Utils.notNull(c, "collection can not be null")
        criteriaType = RelationalOperator.IN
        right =
            ValueNodes.ValueListNode(c)
        return this
    }

    open fun nin(vararg o: Any?): Criteria = nin(java.util.Arrays.asList(*o))

    open fun nin(c: Collection<*>): Criteria {
        Utils.notNull(c, "collection can not be null")
        criteriaType = RelationalOperator.NIN
        right =
            ValueNodes.ValueListNode(c)
        return this
    }

    open fun subsetof(vararg o: Any?): Criteria = subsetof(java.util.Arrays.asList(*o))

    open fun subsetof(c: Collection<*>): Criteria {
        Utils.notNull(c, "collection can not be null")
        criteriaType =
            RelationalOperator.SUBSETOF
        right = ValueNodes.ValueListNode(c)
        return this
    }

    open fun anyof(vararg o: Any?): Criteria = subsetof(java.util.Arrays.asList(*o))

    open fun anyof(c: Collection<*>): Criteria {
        Utils.notNull(c, "collection can not be null")
        criteriaType = RelationalOperator.ANYOF
        right =
            ValueNodes.ValueListNode(c)
        return this
    }

    open fun noneof(vararg o: Any?): Criteria = noneof(java.util.Arrays.asList(*o))

    open fun noneof(c: Collection<*>): Criteria {
        Utils.notNull(c, "collection can not be null")
        criteriaType = RelationalOperator.NONEOF
        right =
            ValueNodes.ValueListNode(c)
        return this
    }

    open fun all(vararg o: Any?): Criteria = all(java.util.Arrays.asList(*o))

    open fun all(c: Collection<*>): Criteria {
        Utils.notNull(c, "collection can not be null")
        criteriaType = RelationalOperator.ALL
        right =
            ValueNodes.ValueListNode(c)
        return this
    }

    @Suppress("SENSELESS_COMPARISON")
    private fun checkComplete() {
        val complete = left != null && criteriaType != null && right != null
        if (!complete) throw JsonPathException("Criteria build exception. Complete on criteria before defining next.")
    }

    companion object {
        @Deprecated("Use where(String)")
        @JvmStatic
        fun where(key: Path): Criteria = Criteria(ValueNode.createPathNode(key))

        @JvmStatic fun where(key: String): Criteria = Criteria(ValueNode.toValueNode(prefixPath(key)))

        @Deprecated("Use Filter.parse")
        @JvmStatic
        @Suppress("DEPRECATION")
        fun parse(criteria: String?): Criteria {
            if (criteria == null) throw InvalidPathException("Criteria can not be null")
            val split =
                java.util.regex.Pattern
                    .compile(" ")
                    .split(criteria.trim { it <= ' ' })
            if (split.size == 3) {
                return create(split[0], split[1], split[2])
            } else if (split.size == 1) {
                return create(split[0], "EXISTS", "true")
            } else {
                throw InvalidPathException("Could not parse criteria")
            }
        }

        @Deprecated("Use where")
        @JvmStatic
        fun create(
            left: String?,
            operator: String,
            right: String?,
        ): Criteria {
            val criteria = Criteria(ValueNode.toValueNode(left))
            criteria.criteriaType = RelationalOperator.fromString(operator)
            criteria.right = ValueNode.toValueNode(right)
            return criteria
        }

        @JvmStatic private fun prefixPath(key: String): String {
            var k = key
            if (!k.startsWith("$") &&
                !k.startsWith("@")
            ) {
                k = "@." + k
            }
            return k
        }
    }
}
