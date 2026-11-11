package com.jayway.jsonpath.internal.filter

import com.jayway.jsonpath.Predicate
import com.jayway.jsonpath.internal.Utils

open class LogicalExpressionNode : ExpressionNode {
    @JvmField
    protected var chain: MutableList<ExpressionNode?> = ArrayList()

    private val operator: LogicalOperator

    private constructor(
        left: ExpressionNode,
        operator: LogicalOperator,
        right: ExpressionNode?,
    ) : super() {
        chain.add(left)
        chain.add(right)
        this.operator = operator
    }

    private constructor(
        operator: LogicalOperator,
        operands: Collection<@JvmSuppressWildcards ExpressionNode>,
    ) : super() {
        chain.addAll(operands)
        this.operator = operator
    }

    open fun and(other: LogicalExpressionNode): LogicalExpressionNode = createLogicalAnd(this, other)

    open fun or(other: LogicalExpressionNode): LogicalExpressionNode = createLogicalOr(this, other)

    open fun getOperator(): LogicalOperator = operator

    open fun append(expressionNode: ExpressionNode): LogicalExpressionNode {
        chain.add(0, expressionNode)
        return this
    }

    override fun toString(): String = "(" + Utils.join(" ${operator.getOperatorString()} ", chain) + ")"

    override fun apply(context: Predicate.PredicateContext): Boolean {
        if (operator == LogicalOperator.OR) {
            for (expression in chain) {
                if (checkNotNull(expression) { "Logical expression is missing an operand" }.apply(context)) {
                    return true
                }
            }
            return false
        } else if (operator == LogicalOperator.AND) {
            for (expression in chain) {
                if (!checkNotNull(expression) { "Logical expression is missing an operand" }.apply(context)) {
                    return false
                }
            }
            return true
        }
        val expression = chain[0]
        return !checkNotNull(expression) { "Logical expression is missing an operand" }.apply(context)
    }

    companion object {
        @JvmStatic
        fun createLogicalNot(operand: ExpressionNode): ExpressionNode = LogicalExpressionNode(operand, LogicalOperator.NOT, null)

        @JvmStatic
        fun createLogicalOr(
            left: ExpressionNode,
            right: ExpressionNode?,
        ): LogicalExpressionNode = LogicalExpressionNode(left, LogicalOperator.OR, right)

        @JvmStatic
        fun createLogicalOr(operands: Collection<@JvmSuppressWildcards ExpressionNode>): LogicalExpressionNode =
            LogicalExpressionNode(LogicalOperator.OR, operands)

        @JvmStatic
        fun createLogicalAnd(
            left: ExpressionNode,
            right: ExpressionNode?,
        ): LogicalExpressionNode = LogicalExpressionNode(left, LogicalOperator.AND, right)

        @JvmStatic
        fun createLogicalAnd(operands: Collection<@JvmSuppressWildcards ExpressionNode>): LogicalExpressionNode =
            LogicalExpressionNode(LogicalOperator.AND, operands)
    }
}
