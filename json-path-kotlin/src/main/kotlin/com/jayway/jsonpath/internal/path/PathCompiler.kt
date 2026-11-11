package com.jayway.jsonpath.internal.path

import com.jayway.jsonpath.InvalidPathException
import com.jayway.jsonpath.Predicate
import com.jayway.jsonpath.internal.CharacterIndex
import com.jayway.jsonpath.internal.Path
import com.jayway.jsonpath.internal.Utils
import com.jayway.jsonpath.internal.filter.FilterCompiler
import com.jayway.jsonpath.internal.function.ParamType
import com.jayway.jsonpath.internal.function.Parameter
import java.util.LinkedList
import java.util.regex.Pattern

open class PathCompiler private constructor(
    private val path: CharacterIndex,
    private val filterStack: LinkedList<Predicate>,
) {
    private constructor(path: String, filterStack: LinkedList<Predicate>) : this(CharacterIndex(path), filterStack)

    private fun compile(): Path {
        val root = readContextToken()
        return CompiledPath(root, root.getPathFragment() == "$")
    }

    private fun readWhitespace() {
        while (path.inBounds()) {
            val c = path.currentChar()
            if (!isWhitespace(c)) break
            path.incrementPosition(1)
        }
    }

    private fun isPathContext(c: Char): Boolean = c == '$' || c == '@'

    private fun readContextToken(): RootPathToken {
        readWhitespace()
        if (!isPathContext(path.currentChar())) throw InvalidPathException("Path must start with '$' or '@'")
        val pathToken = PathTokenFactory.createRootPathToken(path.currentChar())
        if (path.currentIsTail()) return pathToken
        path.incrementPosition(1)
        if (path.currentChar() != '.' &&
            path.currentChar() != '['
        ) {
            fail("Illegal character at position " + path.position() + " expected '.' or '['")
        }
        val appender = pathToken.getPathTokenAppender()
        readNextToken(appender)
        return pathToken
    }

    private fun readNextToken(appender: PathTokenAppender): Boolean {
        when (path.currentChar()) {
            '[' -> {
                if (!readBracketPropertyToken(appender) && !readArrayToken(appender) && !readWildCardToken(appender) &&
                    !readFilterToken(appender) &&
                    !readPlaceholderToken(appender)
                ) {
                    fail("Could not parse token starting at position " + path.position() + ". Expected ?, ', 0-9, * ")
                }
            }

            '.' -> {
                if (!readDotToken(appender)) fail("Could not parse token starting at position " + path.position())
            }

            '*' -> {
                if (!readWildCardToken(appender)) fail("Could not parse token starting at position " + path.position())
            }

            else -> {
                if (!readPropertyOrFunctionToken(appender)) fail("Could not parse token starting at position " + path.position())
            }
        }
        return true
    }

    private fun readDotToken(appender: PathTokenAppender): Boolean {
        if (path.currentCharIs('.') && path.nextCharIs('.')) {
            appender.appendPathToken(PathTokenFactory.crateScanToken())
            path.incrementPosition(2)
        } else if (!path.hasMoreCharacters()) {
            throw InvalidPathException("Path must not end with a '.")
        } else {
            path.incrementPosition(1)
        }
        if (path.currentCharIs('.')) throw InvalidPathException("Character '.' on position " + path.position() + " is not valid.")
        return readNextToken(appender)
    }

    private fun readPropertyOrFunctionToken(appender: PathTokenAppender): Boolean {
        if (path.currentCharIs('[') || path.currentCharIs('*') || path.currentCharIs('.') || path.currentCharIs(' ')) return false
        val startPosition = path.position()
        var readPosition = startPosition
        var endPosition = 0
        var isFunction = false
        while (path.inBounds(readPosition)) {
            val c = path.charAt(readPosition)
            if (c ==
                ' '
            ) {
                throw InvalidPathException(
                    "Use bracket notion ['my prop'] if your property contains blank characters. position: " + path.position(),
                )
            } else if (c == '.' || c == '[') {
                endPosition = readPosition
                break
            } else if (c == '(') {
                isFunction = true
                endPosition = readPosition
                break
            }
            readPosition++
        }
        if (endPosition == 0) endPosition = path.length()
        var functionParameters: List<@JvmSuppressWildcards Parameter>? = null
        if (isFunction) {
            var parenthesisCount = 1
            for (i in readPosition + 1 until path.length()) {
                if (path.charAt(i) == ')') {
                    parenthesisCount--
                } else if (path.charAt(i) == '(') {
                    parenthesisCount++
                }
                if (parenthesisCount == 0) break
            }
            if (parenthesisCount != 0) {
                val functionName = path.subSequence(startPosition, endPosition).toString()
                throw InvalidPathException("Arguments to function: '" + functionName + "' are not closed properly.")
            }
            if (path.inBounds(readPosition + 1)) {
                val c = path.charAt(readPosition + 1)
                if (c != ')') {
                    path.setPosition(endPosition + 1)
                    val functionName = path.subSequence(startPosition, endPosition).toString()
                    functionParameters = parseFunctionParameters(functionName)
                } else {
                    path.setPosition(readPosition + 1)
                }
            } else {
                path.setPosition(readPosition)
            }
        } else {
            path.setPosition(endPosition)
        }
        val property = path.subSequence(startPosition, endPosition).toString()
        if (isFunction) {
            appender.appendPathToken(PathTokenFactory.createFunctionPathToken(property, functionParameters))
        } else {
            appender.appendPathToken(PathTokenFactory.createSinglePropertyPathToken(property, '\''))
        }
        return path.currentIsTail() || readNextToken(appender)
    }

    @Suppress("SENSELESS_COMPARISON")
    private fun parseFunctionParameters(funcName: String): List<@JvmSuppressWildcards Parameter> {
        var type: ParamType? = null
        var groupParen = 1
        var groupBracket = 0
        var groupBrace = 0
        var groupQuote = 0
        var endOfStream = false
        var priorChar = '\u0000'
        val parameters = ArrayList<@JvmSuppressWildcards Parameter>()
        val parameter = StringBuilder()
        while (path.inBounds() && !endOfStream) {
            val c = path.currentChar()
            path.incrementPosition(1)
            if (type == null) {
                if (isWhitespace(c)) continue
                if (c == '{' || Character.isDigit(c) || c == '"' || c == '-') {
                    type = ParamType.JSON
                } else if (isPathContext(c)) {
                    type = ParamType.PATH
                }
            }
            when (c) {
                '"' -> {
                    if (priorChar != '\\' && groupQuote > 0) groupQuote-- else groupQuote++
                }

                '(' -> {
                    groupParen++
                }

                '{' -> {
                    groupBrace++
                }

                '[' -> {
                    groupBracket++
                }

                '}' -> {
                    if (groupBrace ==
                        0
                    ) {
                        throw InvalidPathException("Unexpected close brace '}' at character position: " + path.position())
                    }
                    groupBrace--
                }

                ']' -> {
                    if (groupBracket ==
                        0
                    ) {
                        throw InvalidPathException("Unexpected close bracket ']' at character position: " + path.position())
                    }
                    groupBracket--
                }

                ')', ',' -> {
                    if (c == ')') {
                        groupParen--
                        if (groupParen < 0 || priorChar == '(') parameter.append(c)
                    }
                    if (groupQuote == 0 && groupBrace == 0 && groupBracket == 0 && ((groupParen == 0 && c == ')') || groupParen == 1)) {
                        endOfStream = groupParen == 0
                        if (type != null) {
                            val param =
                                when (type) {
                                    ParamType.JSON -> Parameter(parameter.toString())
                                    ParamType.PATH -> Parameter(PathCompiler(parameter.toString(), LinkedList()).compile())
                                }
                            if (param != null) parameters.add(param)
                            parameter.delete(0, parameter.length)
                            type = null
                        }
                    }
                }
            }
            if (type != null && !(c == ',' && groupBrace == 0 && groupBracket == 0 && groupParen == 1)) parameter.append(c)
            priorChar = c
        }
        if (groupBrace != 0 || groupParen != 0 ||
            groupBracket != 0
        ) {
            throw InvalidPathException("Arguments to function: '" + funcName + "' are not closed properly.")
        }
        return parameters
    }

    private fun isWhitespace(c: Char): Boolean = c == ' ' || c == '\t' || c == '\n' || c == '\r'

    private fun readPlaceholderToken(appender: PathTokenAppender): Boolean {
        if (!path.currentCharIs('[')) return false
        val questionmarkIndex = path.indexOfNextSignificantChar('?')
        if (questionmarkIndex == -1) return false
        val nextSignificantChar = path.nextSignificantChar(questionmarkIndex)
        if (nextSignificantChar != ']' && nextSignificantChar != ',') return false
        val expressionBeginIndex = path.position() + 1
        val expressionEndIndex = path.nextIndexOf(expressionBeginIndex, ']')
        if (expressionEndIndex == -1) return false
        val expression = path.subSequence(expressionBeginIndex, expressionEndIndex).toString()
        val tokens = Pattern.compile(",").split(expression)
        if (filterStack.size <
            tokens.size
        ) {
            throw InvalidPathException("Not enough predicates supplied for filter [" + expression + "] at position " + path.position())
        }
        val predicates = ArrayList<Predicate>()
        for (token in tokens) {
            val trimmed = token.trim { it <= ' ' }
            if ("?" != trimmed) throw InvalidPathException("Expected '?' but found " + trimmed)
            predicates.add(filterStack.pop())
        }
        appender.appendPathToken(PathTokenFactory.createPredicatePathToken(predicates))
        path.setPosition(expressionEndIndex + 1)
        return path.currentIsTail() || readNextToken(appender)
    }

    private fun readFilterToken(appender: PathTokenAppender): Boolean {
        if (!path.currentCharIs('[') && !path.nextSignificantCharIs('?')) return false
        val openStatementBracketIndex = path.position()
        val questionMarkIndex = path.indexOfNextSignificantChar('?')
        if (questionMarkIndex == -1) return false
        val openBracketIndex = path.indexOfNextSignificantChar(questionMarkIndex, '(')
        if (openBracketIndex == -1) return false
        val closeBracketIndex = path.indexOfClosingBracket(openBracketIndex, true, true)
        if (closeBracketIndex == -1) return false
        if (!path.nextSignificantCharIs(closeBracketIndex, ']')) return false
        val closeStatementBracketIndex = path.indexOfNextSignificantChar(closeBracketIndex, ']')
        val criteria = path.subSequence(openStatementBracketIndex, closeStatementBracketIndex + 1).toString()
        val predicate = FilterCompiler.compile(criteria)
        appender.appendPathToken(PathTokenFactory.createPredicatePathToken(predicate))
        path.setPosition(closeStatementBracketIndex + 1)
        return path.currentIsTail() || readNextToken(appender)
    }

    private fun readWildCardToken(appender: PathTokenAppender): Boolean {
        val inBracket = path.currentCharIs('[')
        if (inBracket && !path.nextSignificantCharIs('*')) return false
        if (!path.currentCharIs('*') && path.isOutOfBounds(path.position() + 1)) return false
        if (inBracket) {
            val wildCardIndex = path.indexOfNextSignificantChar('*')
            if (!path.nextSignificantCharIs(wildCardIndex, ']')) {
                throw InvalidPathException("Expected wildcard token to end with ']' on position " + (wildCardIndex + 1))
            }
            val bracketCloseIndex = path.indexOfNextSignificantChar(wildCardIndex, ']')
            path.setPosition(bracketCloseIndex + 1)
        } else {
            path.incrementPosition(1)
        }
        appender.appendPathToken(PathTokenFactory.createWildCardPathToken())
        return path.currentIsTail() || readNextToken(appender)
    }

    private fun readArrayToken(appender: PathTokenAppender): Boolean {
        if (!path.currentCharIs('[')) return false
        val nextSignificantChar = path.nextSignificantChar()
        if (!Character.isDigit(nextSignificantChar) && nextSignificantChar != '-' && nextSignificantChar != ':') return false
        val expressionBeginIndex = path.position() + 1
        val expressionEndIndex = path.nextIndexOf(expressionBeginIndex, ']')
        if (expressionEndIndex == -1) return false
        val expression = path.subSequence(expressionBeginIndex, expressionEndIndex).toString().trim { it <= ' ' }
        if ("*" == expression) return false
        for (c in expression) if (!Character.isDigit(c) && c != ',' && c != '-' && c != ':' && c != ' ') return false
        val isSliceOperation = expression.contains(":")
        if (isSliceOperation) {
            appender.appendPathToken(PathTokenFactory.createSliceArrayPathToken(ArraySliceOperation.parse(expression)))
        } else {
            appender.appendPathToken(PathTokenFactory.createIndexArrayPathToken(ArrayIndexOperation.parse(expression)))
        }
        path.setPosition(expressionEndIndex + 1)
        return path.currentIsTail() || readNextToken(appender)
    }

    private fun readBracketPropertyToken(appender: PathTokenAppender): Boolean {
        if (!path.currentCharIs('[')) return false
        val potentialStringDelimiter = path.nextSignificantChar()
        if (potentialStringDelimiter != '\'' && potentialStringDelimiter != '"') return false
        val properties = ArrayList<String>()
        var startPosition = path.position() + 1
        var readPosition = startPosition
        var endPosition = 0
        var inProperty = false
        var inEscape = false
        var lastSignificantWasComma = false
        while (path.inBounds(readPosition)) {
            val c = path.charAt(readPosition)
            if (inEscape) {
                inEscape = false
            } else if (c == '\\') {
                inEscape = true
            } else if (c == ']' && !inProperty) {
                if (lastSignificantWasComma) fail("Found empty property at index " + readPosition)
                break
            } else if (c == potentialStringDelimiter) {
                if (inProperty) {
                    val nextSignificantChar = path.nextSignificantChar(readPosition)
                    if (nextSignificantChar != ']' &&
                        nextSignificantChar != ','
                    ) {
                        fail(
                            "Property must be separated by comma or Property must be terminated close square bracket at index " +
                                readPosition,
                        )
                    }
                    endPosition = readPosition
                    val prop = path.subSequence(startPosition, endPosition).toString()
                    properties.add(checkNotNull(Utils.unescape(prop)) { "Unescape must preserve a non-null property name" })
                    inProperty = false
                } else {
                    startPosition = readPosition + 1
                    inProperty = true
                    lastSignificantWasComma = false
                }
            } else if (c == ',' && !inProperty) {
                if (lastSignificantWasComma) fail("Found empty property at index " + readPosition)
                lastSignificantWasComma = true
            }
            readPosition++
        }
        if (inProperty) fail("Property has not been closed - missing closing " + potentialStringDelimiter)
        var endBracketIndex = path.indexOfNextSignificantChar(endPosition, ']')
        if (endBracketIndex == -1) fail("Property has not been closed - missing closing ]")
        endBracketIndex++
        path.setPosition(endBracketIndex)
        appender.appendPathToken(PathTokenFactory.createPropertyPathToken(properties, potentialStringDelimiter))
        return path.currentIsTail() || readNextToken(appender)
    }

    companion object {
        @JvmStatic fun compile(
            path: String,
            vararg filters: Predicate,
        ): Path {
            try {
                var ci = CharacterIndex(path)
                ci.trim()
                if (ci.charAt(0) != '$' && ci.charAt(0) != '@') {
                    ci = CharacterIndex("$." + path)
                    ci.trim()
                }
                if (ci.lastCharIs('.')) fail("Path must not end with a '.' or '..'")
                val filterStack = LinkedList<Predicate>(java.util.Arrays.asList(*filters))
                return PathCompiler(ci, filterStack).compile()
            } catch (e: Exception) {
                throw if (e is InvalidPathException) e else InvalidPathException(e)
            }
        }

        @JvmStatic fun fail(message: String): Boolean = throw InvalidPathException(message)
    }
}
