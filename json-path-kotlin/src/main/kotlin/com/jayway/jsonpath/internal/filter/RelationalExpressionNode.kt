package com.jayway.jsonpath.internal.filter
import com.jayway.jsonpath.Predicate
import org.slf4j.LoggerFactory

open class RelationalExpressionNode(
    private val left: ValueNode,
    private val relationalOperator: RelationalOperator,
    private val right: ValueNode,
) : ExpressionNode() {
    init {
        logger.trace("ExpressionNode {}", toString())
    }

    override fun toString(): String {
        if (relationalOperator == RelationalOperator.EXISTS) {
            return left.toString()
        } else {
            return left.toString() + " " + relationalOperator.toString() + " " + right.toString()
        }
    }

    override fun apply(context: Predicate.PredicateContext): Boolean {
        var l = left
        var r = right
        if (left.isPathNode()) l = left.asPathNode().evaluate(context)
        if (right.isPathNode()) r = right.asPathNode().evaluate(context)
        val evaluator = EvaluatorFactory.createEvaluator(relationalOperator)
        if (evaluator != null) return evaluator.evaluate(l, r, context)
        return false
    }

    companion object {
        private val logger = LoggerFactory.getLogger(RelationalExpressionNode::class.java)
    }
}
