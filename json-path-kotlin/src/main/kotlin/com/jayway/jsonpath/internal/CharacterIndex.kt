package com.jayway.jsonpath.internal

import com.jayway.jsonpath.InvalidPathException

open class CharacterIndex(
    private val charSequenceValue: CharSequence,
) {
    private var positionValue = 0
    private var endPositionValue = charSequenceValue.length - 1

    open fun length(): Int = endPositionValue + 1

    open fun charAt(index: Int): Char = charSequenceValue[index]

    open fun currentChar(): Char = charSequenceValue[positionValue]

    open fun currentCharIs(character: Char): Boolean = charSequenceValue[positionValue] == character

    open fun lastCharIs(character: Char): Boolean = charSequenceValue[endPositionValue] == character

    open fun nextCharIs(character: Char): Boolean = inBounds(positionValue + 1) && charSequenceValue[positionValue + 1] == character

    open fun incrementPosition(charCount: Int): Int = setPosition(positionValue + charCount)

    open fun decrementEndPosition(charCount: Int): Int = setEndPosition(endPositionValue - charCount)

    open fun setPosition(newPosition: Int): Int {
        positionValue = newPosition
        return positionValue
    }

    private fun setEndPosition(newPosition: Int): Int {
        endPositionValue = newPosition
        return endPositionValue
    }

    open fun position(): Int = positionValue

    open fun indexOfClosingSquareBracket(startPosition: Int): Int {
        var readPosition = startPosition
        while (inBounds(readPosition)) {
            if (charAt(readPosition) == CLOSE_SQUARE_BRACKET) {
                return readPosition
            }
            readPosition++
        }
        return -1
    }

    open fun indexOfMatchingCloseChar(
        startPosition: Int,
        openChar: Char,
        closeChar: Char,
        skipStrings: Boolean,
        skipRegex: Boolean,
    ): Int {
        if (charAt(startPosition) != openChar) {
            throw InvalidPathException(
                "Expected $openChar but found ${charAt(startPosition)}",
            )
        }

        var opened = 1
        var readPosition = startPosition + 1
        while (inBounds(readPosition)) {
            if (skipStrings) {
                val quoteChar = charAt(readPosition)
                if (quoteChar == SINGLE_QUOTE || quoteChar == DOUBLE_QUOTE) {
                    readPosition = nextIndexOfUnescaped(readPosition, quoteChar)
                    if (readPosition == -1) {
                        throw InvalidPathException(
                            "Could not find matching close quote for " +
                                "$quoteChar when parsing : $charSequenceValue",
                        )
                    }
                    readPosition++
                }
            }
            if (skipRegex) {
                if (charAt(readPosition) == REGEX) {
                    readPosition = nextIndexOfUnescaped(readPosition, REGEX)
                    if (readPosition == -1) {
                        throw InvalidPathException(
                            "Could not find matching close for " +
                                "$REGEX when parsing regex in : $charSequenceValue",
                        )
                    }
                    readPosition++
                }
            }
            if (charAt(readPosition) == openChar) {
                opened++
            }
            if (charAt(readPosition) == closeChar) {
                opened--
                if (opened == 0) {
                    return readPosition
                }
            }
            readPosition++
        }
        return -1
    }

    open fun indexOfClosingBracket(
        startPosition: Int,
        skipStrings: Boolean,
        skipRegex: Boolean,
    ): Int =
        indexOfMatchingCloseChar(
            startPosition,
            OPEN_PARENTHESIS,
            CLOSE_PARENTHESIS,
            skipStrings,
            skipRegex,
        )

    open fun indexOfNextSignificantChar(character: Char): Int = indexOfNextSignificantChar(positionValue, character)

    open fun indexOfNextSignificantChar(
        startPosition: Int,
        character: Char,
    ): Int {
        var readPosition = startPosition + 1
        while (!isOutOfBounds(readPosition) && charAt(readPosition) == SPACE) {
            readPosition++
        }
        return if (charAt(readPosition) == character) {
            readPosition
        } else {
            -1
        }
    }

    open fun nextIndexOf(character: Char): Int = nextIndexOf(positionValue + 1, character)

    open fun nextIndexOf(
        startPosition: Int,
        character: Char,
    ): Int {
        var readPosition = startPosition
        while (!isOutOfBounds(readPosition)) {
            if (charAt(readPosition) == character) {
                return readPosition
            }
            readPosition++
        }
        return -1
    }

    open fun nextIndexOfUnescaped(character: Char): Int = nextIndexOfUnescaped(positionValue, character)

    open fun nextIndexOfUnescaped(
        startPosition: Int,
        character: Char,
    ): Int {
        var readPosition = startPosition + 1
        var inEscape = false
        while (!isOutOfBounds(readPosition)) {
            if (inEscape) {
                inEscape = false
            } else if (ESCAPE == charAt(readPosition)) {
                inEscape = true
            } else if (character == charAt(readPosition)) {
                return readPosition
            }
            readPosition++
        }
        return -1
    }

    open fun charAtOr(
        position: Int,
        defaultChar: Char,
    ): Char =
        if (!inBounds(position)) {
            defaultChar
        } else {
            charAt(position)
        }

    open fun nextSignificantCharIs(
        startPosition: Int,
        character: Char,
    ): Boolean {
        var readPosition = startPosition + 1
        while (!isOutOfBounds(readPosition) && charAt(readPosition) == SPACE) {
            readPosition++
        }
        return !isOutOfBounds(readPosition) && charAt(readPosition) == character
    }

    open fun nextSignificantCharIs(character: Char): Boolean = nextSignificantCharIs(positionValue, character)

    open fun nextSignificantChar(): Char = nextSignificantChar(positionValue)

    open fun nextSignificantChar(startPosition: Int): Char {
        var readPosition = startPosition + 1
        while (!isOutOfBounds(readPosition) && charAt(readPosition) == SPACE) {
            readPosition++
        }
        return if (!isOutOfBounds(readPosition)) {
            charAt(readPosition)
        } else {
            SPACE
        }
    }

    open fun readSignificantChar(character: Char) {
        if (skipBlanks().currentChar() != character) {
            throw InvalidPathException(String.format("Expected character: %c", character))
        }
        incrementPosition(1)
    }

    open fun hasSignificantSubSequence(sequence: CharSequence): Boolean {
        skipBlanks()
        if (!inBounds(positionValue + sequence.length - 1)) {
            return false
        }
        if (subSequence(positionValue, positionValue + sequence.length) != sequence) {
            return false
        }

        incrementPosition(sequence.length)
        return true
    }

    open fun indexOfPreviousSignificantChar(startPosition: Int): Int {
        var readPosition = startPosition - 1
        while (!isOutOfBounds(readPosition) && charAt(readPosition) == SPACE) {
            readPosition--
        }
        return if (!isOutOfBounds(readPosition)) {
            readPosition
        } else {
            -1
        }
    }

    open fun indexOfPreviousSignificantChar(): Int = indexOfPreviousSignificantChar(positionValue)

    open fun previousSignificantChar(startPosition: Int): Char {
        val previousSignificantCharIndex = indexOfPreviousSignificantChar(startPosition)
        return if (previousSignificantCharIndex == -1) {
            SPACE
        } else {
            charAt(previousSignificantCharIndex)
        }
    }

    open fun previousSignificantChar(): Char = previousSignificantChar(positionValue)

    open fun currentIsTail(): Boolean = positionValue >= endPositionValue

    open fun hasMoreCharacters(): Boolean = inBounds(positionValue + 1)

    open fun inBounds(index: Int): Boolean = index >= 0 && index <= endPositionValue

    open fun inBounds(): Boolean = inBounds(positionValue)

    open fun isOutOfBounds(index: Int): Boolean = !inBounds(index)

    open fun subSequence(
        start: Int,
        end: Int,
    ): CharSequence = charSequenceValue.subSequence(start, end)

    open fun charSequence(): CharSequence = charSequenceValue

    override fun toString(): String = charSequenceValue.toString()

    open fun isNumberCharacter(readPosition: Int): Boolean {
        val character = charAt(readPosition)
        return Character.isDigit(character) ||
            character == MINUS ||
            character == PERIOD ||
            character == SCI_UPPER_E ||
            character == SCI_LOWER_E
    }

    open fun skipBlanks(): CharacterIndex {
        while (inBounds() && positionValue < endPositionValue && currentChar() == SPACE) {
            incrementPosition(1)
        }
        return this
    }

    private fun skipBlanksAtEnd(): CharacterIndex {
        while (inBounds() && positionValue < endPositionValue && lastCharIs(SPACE)) {
            decrementEndPosition(1)
        }
        return this
    }

    open fun trim(): CharacterIndex {
        skipBlanks()
        skipBlanksAtEnd()
        return this
    }

    private companion object {
        private const val OPEN_PARENTHESIS = '('
        private const val CLOSE_PARENTHESIS = ')'
        private const val CLOSE_SQUARE_BRACKET = ']'
        private const val SPACE = ' '
        private const val ESCAPE = '\\'
        private const val SINGLE_QUOTE = '\''
        private const val DOUBLE_QUOTE = '"'
        private const val MINUS = '-'
        private const val PERIOD = '.'
        private const val REGEX = '/'
        private const val SCI_UPPER_E = 'E'
        private const val SCI_LOWER_E = 'e'
    }
}
