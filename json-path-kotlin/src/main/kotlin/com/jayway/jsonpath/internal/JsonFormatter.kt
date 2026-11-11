package com.jayway.jsonpath.internal

open class JsonFormatter {
    companion object {
        private const val INDENT = "   "

        private val NEW_LINE = System.lineSeparator()

        private const val MODE_SINGLE = 100
        private const val MODE_DOUBLE = 101
        private const val MODE_ESCAPE_SINGLE = 102
        private const val MODE_ESCAPE_DOUBLE = 103
        private const val MODE_BETWEEN = 104

        private fun appendIndent(
            stringBuilder: StringBuilder,
            count: Int,
        ) {
            var remaining = count
            while (remaining > 0) {
                stringBuilder.append(INDENT)
                --remaining
            }
        }

        @JvmStatic
        fun prettyPrint(input: String): String {
            val normalizedInput = input.replace(Regex("[\\r\\n]"), "")

            val output = StringBuilder(normalizedInput.length * 2)
            var mode = MODE_BETWEEN
            var depth = 0

            for (ch in normalizedInput) {
                when (mode) {
                    MODE_BETWEEN -> {
                        when (ch) {
                            '{', '[' -> {
                                output.append(ch)
                                output.append(NEW_LINE)
                                appendIndent(output, ++depth)
                            }

                            '}', ']' -> {
                                output.append(NEW_LINE)
                                appendIndent(output, --depth)
                                output.append(ch)
                            }

                            ',' -> {
                                output.append(ch)
                                output.append(NEW_LINE)
                                appendIndent(output, depth)
                            }

                            ':' -> {
                                output.append(" : ")
                            }

                            '\'' -> {
                                output.append(ch)
                                mode = MODE_SINGLE
                            }

                            '"' -> {
                                output.append(ch)
                                mode = MODE_DOUBLE
                            }

                            ' ' -> {}

                            else -> {
                                output.append(ch)
                            }
                        }
                    }

                    MODE_ESCAPE_SINGLE -> {
                        output.append(ch)
                        mode = MODE_SINGLE
                    }

                    MODE_ESCAPE_DOUBLE -> {
                        output.append(ch)
                        mode = MODE_DOUBLE
                    }

                    MODE_SINGLE -> {
                        output.append(ch)
                        when (ch) {
                            '\'' -> mode = MODE_BETWEEN
                            '\\' -> mode = MODE_ESCAPE_SINGLE
                        }
                    }

                    MODE_DOUBLE -> {
                        output.append(ch)
                        when (ch) {
                            '"' -> mode = MODE_BETWEEN
                            '\\' -> mode = MODE_ESCAPE_DOUBLE
                        }
                    }
                }
            }
            return output.toString()
        }
    }
}
