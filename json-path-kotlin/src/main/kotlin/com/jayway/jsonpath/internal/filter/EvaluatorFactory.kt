package com.jayway.jsonpath.internal.filter
import com.jayway.jsonpath.JsonPathException
import com.jayway.jsonpath.Predicate
import com.jayway.jsonpath.internal.filter.ValueNodes.PatternNode
import com.jayway.jsonpath.internal.filter.ValueNodes.ValueListNode

open class EvaluatorFactory {
    private open class ExistsEvaluator : Evaluator {
        override fun evaluate(
            left: ValueNode,
            right: ValueNode,
            ctx: Predicate.PredicateContext,
        ): Boolean {
            if (!left.isBooleanNode() && !right.isBooleanNode()) throw JsonPathException("Failed to evaluate exists expression")
            return left.asBooleanNode().getBoolean() == right.asBooleanNode().getBoolean()
        }
    }

    private open class NotEqualsEvaluator : Evaluator {
        override fun evaluate(
            left: ValueNode,
            right: ValueNode,
            ctx: Predicate.PredicateContext,
        ): Boolean = !evaluators.getValue(RelationalOperator.EQ).evaluate(left, right, ctx)
    }

    private open class TypeSafeNotEqualsEvaluator : Evaluator {
        override fun evaluate(
            left: ValueNode,
            right: ValueNode,
            ctx: Predicate.PredicateContext,
        ): Boolean = !evaluators.getValue(RelationalOperator.TSEQ).evaluate(left, right, ctx)
    }

    private open class EqualsEvaluator : Evaluator {
        override fun evaluate(
            left: ValueNode,
            right: ValueNode,
            ctx: Predicate.PredicateContext,
        ): Boolean {
            if (left.isJsonNode() && right.isJsonNode()) {
                return left.asJsonNode().equals(right.asJsonNode(), ctx)
            } else {
                return left.equals(right)
            }
        }
    }

    private open class TypeSafeEqualsEvaluator : Evaluator {
        override fun evaluate(
            left: ValueNode,
            right: ValueNode,
            ctx: Predicate.PredicateContext,
        ): Boolean {
            if (left.javaClass != right.javaClass) return false
            return evaluators.getValue(RelationalOperator.EQ).evaluate(left, right, ctx)
        }
    }

    private open class TypeEvaluator : Evaluator {
        override fun evaluate(
            left: ValueNode,
            right: ValueNode,
            ctx: Predicate.PredicateContext,
        ): Boolean = right.asClassNode().getClazz() == left.type(ctx)
    }

    private open class SizeEvaluator : Evaluator {
        override fun evaluate(
            left: ValueNode,
            right: ValueNode,
            ctx: Predicate.PredicateContext,
        ): Boolean {
            if (!right.isNumberNode()) return false
            val expectedSize = right.asNumberNode().getNumber()?.toInt() ?: return false
            if (left.isStringNode()) {
                return left.asStringNode().length() == expectedSize
            } else if (left.isJsonNode()) {
                return left.asJsonNode().length(ctx) == expectedSize
            }
            return false
        }
    }

    private open class EmptyEvaluator : Evaluator {
        override fun evaluate(
            left: ValueNode,
            right: ValueNode,
            ctx: Predicate.PredicateContext,
        ): Boolean {
            if (left.isStringNode()) {
                return left.asStringNode().isEmpty() == right.asBooleanNode().getBoolean()
            } else if (left.isJsonNode()) {
                return left.asJsonNode().isEmpty(ctx) == right.asBooleanNode().getBoolean()
            }
            return false
        }
    }

    private open class InEvaluator : Evaluator {
        override fun evaluate(
            left: ValueNode,
            right: ValueNode,
            ctx: Predicate.PredicateContext,
        ): Boolean {
            val valueListNode: ValueListNode
            if (right.isJsonNode()) {
                val vn = right.asJsonNode().asValueListNode(ctx)
                if (vn.isUndefinedNode()) return false else valueListNode = vn.asValueListNode()
            } else {
                valueListNode = right.asValueListNode()
            }
            return valueListNode.contains(left)
        }
    }

    private open class NotInEvaluator : Evaluator {
        override fun evaluate(
            left: ValueNode,
            right: ValueNode,
            ctx: Predicate.PredicateContext,
        ): Boolean = !evaluators.getValue(RelationalOperator.IN).evaluate(left, right, ctx)
    }

    private open class AllEvaluator : Evaluator {
        override fun evaluate(
            left: ValueNode,
            right: ValueNode,
            ctx: Predicate.PredicateContext,
        ): Boolean {
            val requiredValues = right.asValueListNode()
            if (left.isJsonNode()) {
                val valueNode = left.asJsonNode().asValueListNode(ctx)
                if (valueNode.isValueListNode()) {
                    val shouldContainAll = valueNode.asValueListNode()
                    for (required in requiredValues) if (!shouldContainAll.contains(required)) return false
                }
                return true
            }
            return false
        }
    }

    private open class ContainsEvaluator : Evaluator {
        override fun evaluate(
            left: ValueNode,
            right: ValueNode,
            ctx: Predicate.PredicateContext,
        ): Boolean {
            if (left.isStringNode() && right.isStringNode()) {
                return left.asStringNode().contains(right.asStringNode().getString())
            } else if (left.isJsonNode()) {
                val valueNode = left.asJsonNode().asValueListNode(ctx)
                if (valueNode.isUndefinedNode()) {
                    return false
                } else {
                    val res = valueNode.asValueListNode().contains(right)
                    return res
                }
            }
            return false
        }
    }

    private open class PredicateMatchEvaluator : Evaluator {
        override fun evaluate(
            left: ValueNode,
            right: ValueNode,
            ctx: Predicate.PredicateContext,
        ): Boolean = right.asPredicateNode().getPredicate().apply(ctx)
    }

    private open class LessThanEvaluator : Evaluator {
        override fun evaluate(
            left: ValueNode,
            right: ValueNode,
            ctx: Predicate.PredicateContext,
        ): Boolean {
            if (left.isNumberNode() &&
                right.isNumberNode()
            ) {
                val leftNumber = left.asNumberNode().getNumber() ?: return false
                val rightNumber = right.asNumberNode().getNumber() ?: return false
                return leftNumber.compareTo(rightNumber) < 0
            }
            if (left.isStringNode() &&
                right.isStringNode()
            ) {
                return left.asStringNode().getString().compareTo(right.asStringNode().getString()) < 0
            }
            if (left.isOffsetDateTimeNode() &&
                right.isOffsetDateTimeNode()
            ) {
                return left.asOffsetDateTimeNode().getDate().compareTo(right.asOffsetDateTimeNode().getDate()) <
                    0
            }
            return false
        }
    }

    private open class LessThanEqualsEvaluator : Evaluator {
        override fun evaluate(
            left: ValueNode,
            right: ValueNode,
            ctx: Predicate.PredicateContext,
        ): Boolean {
            if (left.isNumberNode() &&
                right.isNumberNode()
            ) {
                val leftNumber = left.asNumberNode().getNumber() ?: return false
                val rightNumber = right.asNumberNode().getNumber() ?: return false
                return leftNumber.compareTo(rightNumber) <= 0
            }
            if (left.isStringNode() &&
                right.isStringNode()
            ) {
                return left.asStringNode().getString().compareTo(right.asStringNode().getString()) <= 0
            }
            if (left.isOffsetDateTimeNode() &&
                right.isOffsetDateTimeNode()
            ) {
                return left.asOffsetDateTimeNode().getDate().compareTo(right.asOffsetDateTimeNode().getDate()) <=
                    0
            }
            return false
        }
    }

    private open class GreaterThanEvaluator : Evaluator {
        override fun evaluate(
            left: ValueNode,
            right: ValueNode,
            ctx: Predicate.PredicateContext,
        ): Boolean {
            if (left.isNumberNode() &&
                right.isNumberNode()
            ) {
                val leftNumber = left.asNumberNode().getNumber() ?: return false
                val rightNumber = right.asNumberNode().getNumber() ?: return false
                return leftNumber.compareTo(rightNumber) > 0
            }
            if (left.isStringNode() &&
                right.isStringNode()
            ) {
                return left.asStringNode().getString().compareTo(right.asStringNode().getString()) > 0
            }
            if (left.isOffsetDateTimeNode() &&
                right.isOffsetDateTimeNode()
            ) {
                return left.asOffsetDateTimeNode().getDate().compareTo(right.asOffsetDateTimeNode().getDate()) >
                    0
            }
            return false
        }
    }

    private open class GreaterThanEqualsEvaluator : Evaluator {
        override fun evaluate(
            left: ValueNode,
            right: ValueNode,
            ctx: Predicate.PredicateContext,
        ): Boolean {
            if (left.isNumberNode() &&
                right.isNumberNode()
            ) {
                val leftNumber = left.asNumberNode().getNumber() ?: return false
                val rightNumber = right.asNumberNode().getNumber() ?: return false
                return leftNumber.compareTo(rightNumber) >= 0
            }
            if (left.isStringNode() &&
                right.isStringNode()
            ) {
                return left.asStringNode().getString().compareTo(right.asStringNode().getString()) >= 0
            }
            if (left.isOffsetDateTimeNode() &&
                right.isOffsetDateTimeNode()
            ) {
                return left.asOffsetDateTimeNode().getDate().compareTo(right.asOffsetDateTimeNode().getDate()) >=
                    0
            }
            return false
        }
    }

    private open class SubsetOfEvaluator : Evaluator {
        override fun evaluate(
            left: ValueNode,
            right: ValueNode,
            ctx: Predicate.PredicateContext,
        ): Boolean {
            val rightValueListNode: ValueListNode
            if (right.isJsonNode()) {
                val vn = right.asJsonNode().asValueListNode(ctx)
                if (vn.isUndefinedNode()) return false else rightValueListNode = vn.asValueListNode()
            } else {
                rightValueListNode = right.asValueListNode()
            }
            val leftValueListNode: ValueListNode
            if (left.isJsonNode()) {
                val vn = left.asJsonNode().asValueListNode(ctx)
                if (vn.isUndefinedNode()) return false else leftValueListNode = vn.asValueListNode()
            } else {
                leftValueListNode = left.asValueListNode()
            }
            return leftValueListNode.subsetof(rightValueListNode)
        }
    }

    private open class AnyOfEvaluator : Evaluator {
        override fun evaluate(
            left: ValueNode,
            right: ValueNode,
            ctx: Predicate.PredicateContext,
        ): Boolean {
            val rightValueListNode: ValueListNode
            if (right.isJsonNode()) {
                val vn = right.asJsonNode().asValueListNode(ctx)
                if (vn.isUndefinedNode()) return false else rightValueListNode = vn.asValueListNode()
            } else {
                rightValueListNode = right.asValueListNode()
            }
            val leftValueListNode: ValueListNode
            if (left.isJsonNode()) {
                val vn = left.asJsonNode().asValueListNode(ctx)
                if (vn.isUndefinedNode()) return false else leftValueListNode = vn.asValueListNode()
            } else {
                leftValueListNode = left.asValueListNode()
            }
            for (leftValueNode in leftValueListNode) {
                for (rightValueNode in rightValueListNode) {
                    if (leftValueNode.equals(
                            rightValueNode,
                        )
                    ) {
                        return true
                    }
                }
            }
            return false
        }
    }

    private open class NoneOfEvaluator : Evaluator {
        override fun evaluate(
            left: ValueNode,
            right: ValueNode,
            ctx: Predicate.PredicateContext,
        ): Boolean {
            val rightValueListNode: ValueListNode
            if (right.isJsonNode()) {
                val vn = right.asJsonNode().asValueListNode(ctx)
                if (vn.isUndefinedNode()) return false else rightValueListNode = vn.asValueListNode()
            } else {
                rightValueListNode = right.asValueListNode()
            }
            val leftValueListNode: ValueListNode
            if (left.isJsonNode()) {
                val vn = left.asJsonNode().asValueListNode(ctx)
                if (vn.isUndefinedNode()) return false else leftValueListNode = vn.asValueListNode()
            } else {
                leftValueListNode = left.asValueListNode()
            }
            for (leftValueNode in leftValueListNode) {
                for (rightValueNode in rightValueListNode) {
                    if (leftValueNode.equals(
                            rightValueNode,
                        )
                    ) {
                        return false
                    }
                }
            }
            return true
        }
    }

    private open class RegexpEvaluator : Evaluator {
        override fun evaluate(
            left: ValueNode,
            right: ValueNode,
            ctx: Predicate.PredicateContext,
        ): Boolean {
            if (!(left.isPatternNode() xor right.isPatternNode())) return false
            if (left.isPatternNode()) {
                if (right.isValueListNode() ||
                    (right.isJsonNode() && right.asJsonNode().isArray(ctx))
                ) {
                    return matchesAny(left.asPatternNode(), right.asJsonNode().asValueListNode(ctx))
                } else {
                    return matches(left.asPatternNode(), getInput(right))
                }
            } else {
                if (left.isValueListNode() ||
                    (left.isJsonNode() && left.asJsonNode().isArray(ctx))
                ) {
                    return matchesAny(right.asPatternNode(), left.asJsonNode().asValueListNode(ctx))
                } else {
                    return matches(right.asPatternNode(), getInput(left))
                }
            }
        }

        private fun matches(
            patternNode: PatternNode,
            inputToMatch: String,
        ): Boolean = patternNode.getCompiledPattern().matcher(inputToMatch).matches()

        private fun matchesAny(
            patternNode: PatternNode,
            valueNode: ValueNode,
        ): Boolean {
            if (!valueNode.isValueListNode()) return false
            val listNode = valueNode.asValueListNode()
            val pattern = patternNode.getCompiledPattern()
            val it = listNode.iterator()
            while (it.hasNext()) {
                val input = getInput(it.next())
                if (pattern.matcher(input).matches()) return true
            }
            return false
        }

        private fun getInput(valueNode: ValueNode): String {
            var input = ""
            if (valueNode.isStringNode() || valueNode.isNumberNode()) {
                input = valueNode.asStringNode().getString()
            } else if (valueNode.isBooleanNode()) {
                input = valueNode.asBooleanNode().toString()
            }
            return input
        }
    }

    companion object {
        private val evaluators: MutableMap<RelationalOperator, Evaluator> = HashMap()

        init {
            evaluators[RelationalOperator.EXISTS] = ExistsEvaluator()
            evaluators[RelationalOperator.NE] = NotEqualsEvaluator()
            evaluators[RelationalOperator.TSNE] = TypeSafeNotEqualsEvaluator()
            evaluators[RelationalOperator.EQ] = EqualsEvaluator()
            evaluators[RelationalOperator.TSEQ] = TypeSafeEqualsEvaluator()
            evaluators[RelationalOperator.LT] = LessThanEvaluator()
            evaluators[RelationalOperator.LTE] = LessThanEqualsEvaluator()
            evaluators[RelationalOperator.GT] = GreaterThanEvaluator()
            evaluators[RelationalOperator.GTE] = GreaterThanEqualsEvaluator()
            evaluators[RelationalOperator.REGEX] = RegexpEvaluator()
            evaluators[RelationalOperator.SIZE] = SizeEvaluator()
            evaluators[RelationalOperator.EMPTY] = EmptyEvaluator()
            evaluators[RelationalOperator.IN] = InEvaluator()
            evaluators[RelationalOperator.NIN] = NotInEvaluator()
            evaluators[RelationalOperator.ALL] = AllEvaluator()
            evaluators[RelationalOperator.CONTAINS] = ContainsEvaluator()
            evaluators[RelationalOperator.MATCHES] = PredicateMatchEvaluator()
            evaluators[RelationalOperator.TYPE] = TypeEvaluator()
            evaluators[RelationalOperator.SUBSETOF] = SubsetOfEvaluator()
            evaluators[RelationalOperator.ANYOF] = AnyOfEvaluator()
            evaluators[RelationalOperator.NONEOF] = NoneOfEvaluator()
        }

        @JvmStatic fun createEvaluator(operator: RelationalOperator): Evaluator? = evaluators[operator]
    }
}
