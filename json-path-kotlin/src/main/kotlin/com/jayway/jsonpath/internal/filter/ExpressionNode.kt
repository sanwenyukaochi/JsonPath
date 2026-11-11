package com.jayway.jsonpath.internal.filter

import com.jayway.jsonpath.Predicate

abstract class ExpressionNode : Predicate {
    companion object {
        @JvmStatic
        fun createExpressionNode(
            right: ExpressionNode,
            operator: LogicalOperator,
            left: ExpressionNode,
        ): ExpressionNode {
            if (operator == LogicalOperator.AND) {
                return if (right is LogicalExpressionNode &&
                    right.getOperator() == LogicalOperator.AND
                ) {
                    right.append(left)
                } else {
                    LogicalExpressionNode.createLogicalAnd(left, right)
                }
            }
            return if (right is LogicalExpressionNode &&
                right.getOperator() == LogicalOperator.OR
            ) {
                right.append(left)
            } else {
                LogicalExpressionNode.createLogicalOr(left, right)
            }
        }
    }
}
