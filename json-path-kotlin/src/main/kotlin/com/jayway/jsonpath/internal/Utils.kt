package com.jayway.jsonpath.internal

import com.jayway.jsonpath.JsonPathException
import java.io.Closeable
import java.io.IOException
import java.io.StringWriter
import java.util.Locale

class Utils private constructor() {
    companion object {
        @JvmStatic
        fun join(
            delimiter: String?,
            wrap: String?,
            objs: Iterable<*>,
        ): String {
            val iter = objs.iterator()
            if (!iter.hasNext()) {
                return ""
            }
            val buffer = StringBuilder()
            buffer.append(wrap).append(iter.next()).append(wrap)
            while (iter.hasNext()) {
                buffer
                    .append(delimiter)
                    .append(wrap)
                    .append(iter.next())
                    .append(wrap)
            }
            return buffer.toString()
        }

        @JvmStatic
        fun join(
            delimiter: String?,
            objs: Iterable<*>,
        ): String = join(delimiter, "", objs)

        @JvmStatic
        fun concat(vararg strings: CharSequence): String {
            if (strings.isEmpty()) {
                return ""
            }
            if (strings.size == 1) {
                return strings[0].toString()
            }
            var length = 0

            var indexOfSingleNonEmptyString = -1
            for (i in strings.indices) {
                val charSequence = strings[i]
                val len = charSequence.length
                length += len
                if (indexOfSingleNonEmptyString != -2 && len > 0) {
                    indexOfSingleNonEmptyString =
                        if (indexOfSingleNonEmptyString == -1) {
                            i
                        } else {
                            -2
                        }
                }
            }
            if (length == 0) {
                return ""
            }
            if (indexOfSingleNonEmptyString > 0) {
                return strings[indexOfSingleNonEmptyString].toString()
            }
            val stringBuilder = StringBuilder(length)
            for (charSequence in strings) {
                stringBuilder.append(charSequence)
            }
            return stringBuilder.toString()
        }

        @JvmStatic
        fun closeQuietly(closeable: Closeable?) {
            try {
                closeable?.close()
            } catch (_: IOException) {
            }
        }

        @JvmStatic
        fun escape(
            str: String?,
            escapeSingleQuote: Boolean,
        ): String? {
            if (str == null) {
                return null
            }
            val len = str.length
            val writer = StringWriter(len * 2)

            for (i in 0 until len) {
                val ch = str[i]

                if (ch.code > 0xfff) {
                    writer.write("\\u" + hex(ch))
                } else if (ch.code > 0xff) {
                    writer.write("\\u0" + hex(ch))
                } else if (ch.code > 0x7f) {
                    writer.write("\\u00" + hex(ch))
                } else if (ch.code < 32) {
                    when (ch) {
                        '\b' -> {
                            writer.write('\\'.code)
                            writer.write('b'.code)
                        }

                        '\n' -> {
                            writer.write('\\'.code)
                            writer.write('n'.code)
                        }

                        '\t' -> {
                            writer.write('\\'.code)
                            writer.write('t'.code)
                        }

                        '\u000C' -> {
                            writer.write('\\'.code)
                            writer.write('f'.code)
                        }

                        '\r' -> {
                            writer.write('\\'.code)
                            writer.write('r'.code)
                        }

                        else -> {
                            if (ch.code > 0xf) {
                                writer.write("\\u00" + hex(ch))
                            } else {
                                writer.write("\\u000" + hex(ch))
                            }
                        }
                    }
                } else {
                    when (ch) {
                        '\'' -> {
                            if (escapeSingleQuote) {
                                writer.write('\\'.code)
                            }
                            writer.write('\''.code)
                        }

                        '"' -> {
                            writer.write('\\'.code)
                            writer.write('"'.code)
                        }

                        '\\' -> {
                            writer.write('\\'.code)
                            writer.write('\\'.code)
                        }

                        '/' -> {
                            writer.write('\\'.code)
                            writer.write('/'.code)
                        }

                        else -> {
                            writer.write(ch.code)
                        }
                    }
                }
            }
            return writer.toString()
        }

        @JvmStatic
        fun unescape(str: String?): String? {
            if (str == null) {
                return null
            }
            val len = str.length
            val writer = StringWriter(len)
            val unicode = StringBuilder(4)
            var hadSlash = false
            var inUnicode = false
            for (i in 0 until len) {
                val ch = str[i]
                if (inUnicode) {
                    unicode.append(ch)
                    if (unicode.length == 4) {
                        try {
                            val value = Integer.parseInt(unicode.toString(), 16)
                            writer.write(value)
                            unicode.setLength(0)
                            inUnicode = false
                            hadSlash = false
                        } catch (numberFormatException: NumberFormatException) {
                            throw JsonPathException(
                                "Unable to parse unicode value: $unicode",
                                numberFormatException,
                            )
                        }
                    }
                    continue
                }
                if (hadSlash) {
                    hadSlash = false
                    when (ch) {
                        '\\' -> writer.write('\\'.code)
                        '\'' -> writer.write('\''.code)
                        '"' -> writer.write('"'.code)
                        'r' -> writer.write('\r'.code)
                        'f' -> writer.write('\u000C'.code)
                        't' -> writer.write('\t'.code)
                        'n' -> writer.write('\n'.code)
                        'b' -> writer.write('\b'.code)
                        'u' -> inUnicode = true
                        else -> writer.write(ch.code)
                    }
                    continue
                } else if (ch == '\\') {
                    hadSlash = true
                    continue
                }
                writer.write(ch.code)
            }
            if (hadSlash) {
                writer.write('\\'.code)
            }
            return writer.toString()
        }

        @JvmStatic
        fun hex(ch: Char): String = Integer.toHexString(ch.code).uppercase(Locale.getDefault())

        @JvmStatic
        fun isEmpty(charSequence: CharSequence?): Boolean = charSequence.isNullOrEmpty()

        @JvmStatic
        fun indexOf(
            charSequence: CharSequence,
            searchChar: CharSequence,
            start: Int,
        ): Int = charSequence.toString().indexOf(searchChar.toString(), start)

        @JvmStatic
        fun <T> notNull(
            value: T?,
            message: String,
        ): T {
            if (value == null) {
                throw IllegalArgumentException(message)
            }
            return value
        }

        @JvmStatic
        fun <T> notNull(
            value: T?,
            message: String,
            vararg values: Any?,
        ): T {
            if (value == null) {
                throw IllegalArgumentException(String.format(message, *values))
            }
            return value
        }

        @JvmStatic
        fun isTrue(
            expression: Boolean,
            message: String,
        ) {
            if (!expression) {
                throw IllegalArgumentException(message)
            }
        }

        @JvmStatic
        fun onlyOneIsTrue(
            message: String,
            vararg expressions: Boolean,
        ) {
            if (!onlyOneIsTrueNonThrow(*expressions)) {
                throw IllegalArgumentException(message)
            }
        }

        @JvmStatic
        fun onlyOneIsTrueNonThrow(vararg expressions: Boolean): Boolean {
            var count = 0
            for (expression in expressions) {
                if (expression) {
                    ++count
                    if (count > 1) {
                        return false
                    }
                }
            }
            return count == 1
        }

        @JvmStatic
        fun <T : CharSequence> notEmpty(
            chars: T?,
            message: String,
        ): T {
            if (chars.isNullOrEmpty()) {
                throw IllegalArgumentException(message)
            }
            return chars
        }

        @JvmStatic
        fun notEmpty(
            bytes: ByteArray?,
            message: String,
        ): ByteArray {
            if (bytes == null || bytes.isEmpty()) {
                throw IllegalArgumentException(message)
            }
            return bytes
        }

        @JvmStatic
        fun <T : CharSequence> notEmpty(
            chars: T?,
            message: String,
            vararg values: Any?,
        ): T {
            if (chars.isNullOrEmpty()) {
                throw IllegalArgumentException(String.format(message, *values))
            }
            return chars
        }

        @JvmStatic
        fun toString(value: Any?): String? {
            if (value == null) {
                return null
            }
            return value.toString()
        }
    }
}
