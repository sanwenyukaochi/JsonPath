package com.jayway.jsonpath.internal.filter
import com.jayway.jsonpath.Filter
import com.jayway.jsonpath.InvalidPathException
import com.jayway.jsonpath.Predicate
import com.jayway.jsonpath.internal.CharacterIndex
import org.slf4j.LoggerFactory

open class FilterCompiler private constructor(
    filterString: String,
) {
    private var filter = CharacterIndex(filterString)

    init {
        filter.trim()
        if (!filter.currentCharIs('[') ||
            !filter.lastCharIs(']')
        ) {
            throw InvalidPathException("Filter must start with '[' and end with ']'. " + filterString)
        }
        filter.incrementPosition(1)
        filter.decrementEndPosition(1)
        filter.trim()
        if (!filter.currentCharIs('?')) throw InvalidPathException("Filter must start with '[?' and end with ']'. " + filterString)
        filter.incrementPosition(1)
        filter.trim()
        if (!filter.currentCharIs('(') ||
            !filter.lastCharIs(')')
        ) {
            throw InvalidPathException("Filter must start with '[?(' and end with ')]'. " + filterString)
        }
    }

    open fun compile(): Predicate {
        try {
            val result = readLogicalOR()
            filter.skipBlanks()
            if (filter.inBounds()) {
                throw InvalidPathException(
                    String.format(
                        "Expected end of filter expression instead of: %s",
                        filter.subSequence(filter.position(), filter.length()),
                    ),
                )
            }
            return result
        } catch (e: InvalidPathException) {
            throw e
        } catch (_: Exception) {
            throw InvalidPathException(
                "Failed to parse filter: " + filter + ", error on position: " + filter.position() + ", char: " + filter.currentChar(),
            )
        }
    }

    private fun readValueNode(): ValueNode =
        when (filter.skipBlanks().currentChar()) {
            '$', '@' -> {
                readPath()
            }

            '!' -> {
                filter.incrementPosition(1)
                when (filter.skipBlanks().currentChar()) {
                    '$', '@' -> readPath()
                    else -> throw InvalidPathException(String.format("Unexpected character: %c", '!'))
                }
            }

            else -> {
                readLiteral()
            }
        }

    private fun readLiteral(): ValueNode =
        when (filter.skipBlanks().currentChar()) {
            '\'' -> readStringLiteral('\'')
            '"' -> readStringLiteral('"')
            't', 'f' -> readBooleanLiteral()
            '-' -> readNumberLiteral()
            'n' -> readNullLiteral()
            '{', '[' -> readJsonLiteral()
            '/' -> readPattern()
            else -> readNumberLiteral()
        }

    private fun readLogicalOR(): ExpressionNode {
        val ops = ArrayList<ExpressionNode>()
        ops.add(readLogicalAND())
        while (true) {
            val savepoint = filter.position()
            if (filter.hasSignificantSubSequence(LogicalOperator.OR.getOperatorString())) {
                ops.add(readLogicalAND())
            } else {
                filter.setPosition(savepoint)
                break
            }
        }
        return if (ops.size == 1) ops[0] else LogicalExpressionNode.createLogicalOr(ops)
    }

    private fun readLogicalAND(): ExpressionNode {
        val ops = ArrayList<ExpressionNode>()
        ops.add(readLogicalANDOperand())
        while (true) {
            val savepoint = filter.position()
            if (filter.hasSignificantSubSequence(LogicalOperator.AND.getOperatorString())) {
                ops.add(readLogicalANDOperand())
            } else {
                filter.setPosition(savepoint)
                break
            }
        }
        return if (ops.size == 1) ops[0] else LogicalExpressionNode.createLogicalAnd(ops)
    }

    private fun readLogicalANDOperand(): ExpressionNode {
        val savepoint = filter.skipBlanks().position()
        if (filter.skipBlanks().currentCharIs('!')) {
            filter.readSignificantChar('!')
            when (filter.skipBlanks().currentChar()) {
                '$', '@' -> {
                    filter.setPosition(savepoint)
                }

                else -> {
                    val op = readLogicalANDOperand()
                    return LogicalExpressionNode.createLogicalNot(op)
                }
            }
        }
        if (filter.skipBlanks().currentCharIs('(')) {
            filter.readSignificantChar('(')
            val op = readLogicalOR()
            filter.readSignificantChar(')')
            return op
        }
        return readExpression()
    }

    private fun readExpression(): RelationalExpressionNode {
        var left = readValueNode()
        val savepoint = filter.position()
        try {
            val operator = readRelationalOperator()
            val right = readValueNode()
            return RelationalExpressionNode(left, operator, right)
        } catch (_: InvalidPathException) {
            filter.setPosition(savepoint)
        }
        val pathNode = left.asPathNode()
        left = pathNode.asExistsCheck(pathNode.shouldExists())
        val operator = RelationalOperator.EXISTS
        val right = if (left.asPathNode().shouldExists()) ValueNodes.TRUE else ValueNodes.FALSE
        return RelationalExpressionNode(left, operator, right)
    }

    private fun readLogicalOperator(): LogicalOperator {
        val begin = filter.skipBlanks().position()
        val end = begin + 1
        if (!filter.inBounds(end)) throw InvalidPathException("Expected boolean literal")
        val logicalOperator = filter.subSequence(begin, end + 1)
        if (logicalOperator != "||" && logicalOperator != "&&") throw InvalidPathException("Expected logical operator")
        filter.incrementPosition(logicalOperator.length)
        logger.trace("LogicalOperator from {} to {} -> [{}]", begin, end, logicalOperator)
        return LogicalOperator.fromString(logicalOperator.toString())
    }

    private fun readRelationalOperator(): RelationalOperator {
        val begin = filter.skipBlanks().position()
        if (isRelationalOperatorChar(filter.currentChar())) {
            while (filter.inBounds() && isRelationalOperatorChar(filter.currentChar())) filter.incrementPosition(1)
        } else {
            while (filter.inBounds() && filter.currentChar() != ' ') filter.incrementPosition(1)
        }
        val operator = filter.subSequence(begin, filter.position())
        logger.trace("Operator from {} to {} -> [{}]", begin, filter.position() - 1, operator)
        return RelationalOperator.fromString(operator.toString())
    }

    private fun readNullLiteral(): ValueNodes.NullNode {
        val begin = filter.position()
        if (filter.currentChar() == 'n' && filter.inBounds(filter.position() + 3)) {
            val nullValue = filter.subSequence(filter.position(), filter.position() + 4)
            if ("null" == nullValue.toString()) {
                logger.trace("NullLiteral from {} to {} -> [{}]", begin, filter.position() + 3, nullValue)
                filter.incrementPosition(nullValue.length)
                return ValueNode.createNullNode()
            }
        }
        throw InvalidPathException("Expected <null> value")
    }

    private fun readJsonLiteral(): ValueNodes.JsonNode {
        val begin = filter.position()
        val openChar = filter.currentChar()
        if (ASSERTIONS_ENABLED && !(openChar == '[' || openChar == '{')) {
            throw AssertionError()
        }
        val closeChar = if (openChar == '[') ']' else '}'
        val closingIndex = filter.indexOfMatchingCloseChar(filter.position(), openChar, closeChar, true, false)
        if (closingIndex == -1) {
            throw InvalidPathException("String not closed. Expected ' in " + filter)
        } else {
            filter.setPosition(closingIndex + 1)
        }
        val json = filter.subSequence(begin, filter.position())
        logger.trace("JsonLiteral from {} to {} -> [{}]", begin, filter.position(), json)
        return ValueNode.createJsonNode(json)
    }

    private fun endOfFlags(position: Int): Int {
        var endIndex = position
        val currentChar = CharArray(1)
        while (filter.inBounds(endIndex)) {
            currentChar[0] = filter.charAt(endIndex)
            if (PatternFlag.parseFlags(currentChar) > 0) {
                endIndex++
                continue
            }
            break
        }
        return endIndex
    }

    private fun readPattern(): ValueNodes.PatternNode {
        val begin = filter.position()
        var closingIndex = filter.nextIndexOfUnescaped('/')
        if (closingIndex == -1) {
            throw InvalidPathException("Pattern not closed. Expected / in " + filter)
        } else {
            if (filter.inBounds(closingIndex + 1)) {
                val endFlagsIndex = endOfFlags(closingIndex + 1)
                if (endFlagsIndex > closingIndex) {
                    val flags = filter.subSequence(closingIndex + 1, endFlagsIndex)
                    closingIndex += flags.length
                }
            }
            filter.setPosition(closingIndex + 1)
        }
        val pattern = filter.subSequence(begin, filter.position())
        logger.trace("PatternNode from {} to {} -> [{}]", begin, filter.position(), pattern)
        return ValueNode.createPatternNode(pattern)
    }

    private fun readStringLiteral(endChar: Char): ValueNodes.StringNode {
        val begin = filter.position()
        val closingSingleQuoteIndex = filter.nextIndexOfUnescaped(endChar)
        if (closingSingleQuoteIndex ==
            -1
        ) {
            throw InvalidPathException("String literal does not have matching quotes. Expected " + endChar + " in " + filter)
        } else {
            filter.setPosition(closingSingleQuoteIndex + 1)
        }
        val stringLiteral = filter.subSequence(begin, filter.position())
        logger.trace("StringLiteral from {} to {} -> [{}]", begin, filter.position(), stringLiteral)
        return ValueNode.createStringNode(stringLiteral, true)
    }

    private fun readNumberLiteral(): ValueNodes.NumberNode {
        val begin = filter.position()
        while (filter.inBounds() && filter.isNumberCharacter(filter.position())) filter.incrementPosition(1)
        val numberLiteral = filter.subSequence(begin, filter.position())
        logger.trace("NumberLiteral from {} to {} -> [{}]", begin, filter.position(), numberLiteral)
        return ValueNode.createNumberNode(numberLiteral)
    }

    private fun readBooleanLiteral(): ValueNodes.BooleanNode {
        val begin = filter.position()
        val end = if (filter.currentChar() == 't') filter.position() + 3 else filter.position() + 4
        if (!filter.inBounds(end)) throw InvalidPathException("Expected boolean literal")
        val boolValue = filter.subSequence(begin, end + 1)
        if (boolValue != "true" && boolValue != "false") throw InvalidPathException("Expected boolean literal")
        filter.incrementPosition(boolValue.length)
        logger.trace("BooleanLiteral from {} to {} -> [{}]", begin, end, boolValue)
        return ValueNode.createBooleanNode(boolValue)
    }

    private fun readPath(): ValueNodes.PathNode {
        val previousSignificantChar = filter.previousSignificantChar()
        val begin = filter.position()
        filter.incrementPosition(1)
        while (filter.inBounds()) {
            if (filter.currentChar() == '[') {
                val closingSquareBracketIndex = filter.indexOfMatchingCloseChar(filter.position(), '[', ']', true, false)
                if (closingSquareBracketIndex == -1) {
                    throw InvalidPathException("Square brackets does not match in filter " + filter)
                } else {
                    filter.setPosition(closingSquareBracketIndex + 1)
                }
            }
            val closingFunctionBracket = filter.currentChar() == ')' && currentCharIsClosingFunctionBracket(begin)
            val closingLogicalBracket = filter.currentChar() == ')' && !closingFunctionBracket
            if (!filter.inBounds() || isRelationalOperatorChar(filter.currentChar()) || filter.currentChar() == ' ' ||
                closingLogicalBracket
            ) {
                break
            } else {
                filter.incrementPosition(1)
            }
        }
        val shouldExists = previousSignificantChar != '!'
        val path = filter.subSequence(begin, filter.position())
        return ValueNode.createPathNode(path, false, shouldExists)
    }

    private fun expressionIsTerminated(): Boolean {
        var c = filter.currentChar()
        if (c == ')' || isLogicalOperatorChar(c)) return true
        c = filter.nextSignificantChar()
        if (c == ')' || isLogicalOperatorChar(c)) return true
        return false
    }

    private fun currentCharIsClosingFunctionBracket(lowerBound: Int): Boolean {
        if (filter.currentChar() != ')') return false
        var idx = filter.indexOfPreviousSignificantChar()
        if (idx == -1 || filter.charAt(idx) != '(') return false
        idx--
        while (filter.inBounds(idx) && idx > lowerBound) {
            if (filter.charAt(idx) == '.') return true
            idx--
        }
        return false
    }

    private fun isLogicalOperatorChar(c: Char): Boolean = c == '&' || c == '|'

    private fun isRelationalOperatorChar(c: Char): Boolean = c == '<' || c == '>' || c == '=' || c == '~' || c == '!'

    private class CompiledFilter(
        private val predicate: Predicate,
    ) : Filter() {
        override fun apply(context: Predicate.PredicateContext): Boolean = predicate.apply(context)

        override fun toString(): String {
            val predicateString = predicate.toString()
            if (predicateString.startsWith("(")) {
                return "[?" + predicateString + "]"
            } else {
                return "[?(" + predicateString + ")]"
            }
        }
    }

    companion object {
        private val ASSERTIONS_ENABLED = FilterCompiler::class.java.desiredAssertionStatus()
        private val logger = LoggerFactory.getLogger(FilterCompiler::class.java)

        @JvmStatic fun compile(filterString: String): Filter {
            val compiler = FilterCompiler(filterString)
            return CompiledFilter(compiler.compile())
        }
    }
}
