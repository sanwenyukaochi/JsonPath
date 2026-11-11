package com.jayway.jsonpath.internal.filter

import java.util.regex.Pattern

enum class PatternFlag(
    private val code: Int,
    private val flag: Char,
) {
    UNIX_LINES(Pattern.UNIX_LINES, 'd'),
    CASE_INSENSITIVE(Pattern.CASE_INSENSITIVE, 'i'),
    COMMENTS(Pattern.COMMENTS, 'x'),
    MULTILINE(Pattern.MULTILINE, 'm'),
    DOTALL(Pattern.DOTALL, 's'),
    UNICODE_CASE(Pattern.UNICODE_CASE, 'u'),
    UNICODE_CHARACTER_CLASS(Pattern.UNICODE_CHARACTER_CLASS, 'U'),
    ;

    companion object {
        @JvmStatic
        fun parseFlags(flags: CharArray): Int {
            var flagsValue = 0
            for (flag in flags) {
                flagsValue = flagsValue or getCodeByFlag(flag)
            }
            return flagsValue
        }

        @JvmStatic
        fun parseFlags(flags: Int): String {
            val builder = StringBuilder()
            for (patternFlag in entries) {
                if ((patternFlag.code and flags) == patternFlag.code) {
                    builder.append(patternFlag.flag)
                }
            }
            return builder.toString()
        }

        private fun getCodeByFlag(flag: Char): Int {
            for (patternFlag in entries) {
                if (patternFlag.flag == flag) {
                    return patternFlag.code
                }
            }
            return 0
        }
    }
}
