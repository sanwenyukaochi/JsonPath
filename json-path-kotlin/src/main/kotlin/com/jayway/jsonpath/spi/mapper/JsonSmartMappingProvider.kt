package com.jayway.jsonpath.spi.mapper

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.TypeRef
import net.minidev.json.JSONValue
import net.minidev.json.writer.JsonReader
import net.minidev.json.writer.JsonReaderI
import java.math.BigDecimal
import java.math.BigInteger
import java.text.DateFormat
import java.text.ParseException
import java.util.Date
import java.util.concurrent.Callable

open class JsonSmartMappingProvider(
    private val factory: Callable<JsonReader>,
) : MappingProvider {
    constructor(jsonReader: JsonReader) : this(Callable { jsonReader })

    constructor() : this(DEFAULT)

    override fun <T> map(
        source: Any?,
        targetType: Class<T>,
        configuration: Configuration,
    ): T? {
        if (source == null) {
            return null
        }
        if (targetType.isAssignableFrom(source.javaClass)) {
            @Suppress("UNCHECKED_CAST")
            return source as T
        }
        try {
            if (!configuration.jsonProvider().isMap(source) &&
                !configuration.jsonProvider().isArray(source)
            ) {
                return checkNotNull(factory.call()) { "JsonReader factory returned null" }.getMapper(targetType).convert(source)
            }
            val json = configuration.jsonProvider().toJson(source)
            return JSONValue.parse(json, targetType)
        } catch (exception: Exception) {
            throw MappingException(exception)
        }
    }

    override fun <T> map(
        source: Any?,
        targetType: TypeRef<T>,
        configuration: Configuration,
    ): T? =
        throw UnsupportedOperationException(
            "Json-smart provider does not support TypeRef! Use a Jackson or Gson based provider",
        )

    private class StringReader : JsonReaderI<String>(null) {
        override fun convert(src: Any?): String? {
            if (src == null) {
                return null
            }
            return src.toString()
        }
    }

    private class IntegerReader : JsonReaderI<Int>(null) {
        override fun convert(src: Any?): Int? {
            if (src == null) {
                return null
            }
            if (src is Int) {
                return src
            } else if (src is Long) {
                return src.toInt()
            } else if (src is Double) {
                return src.toInt()
            } else if (src is BigDecimal) {
                return src.toInt()
            } else if (src is Float) {
                return src.toInt()
            } else if (src is String) {
                return Integer.valueOf(src)
            }
            throw MappingException(
                "can not map a ${src.javaClass} to ${Int::class.javaObjectType.name}",
            )
        }
    }

    private class LongReader : JsonReaderI<Long>(null) {
        override fun convert(src: Any?): Long? {
            if (src == null) {
                return null
            }
            if (src is Long) {
                return src
            } else if (src is Int) {
                return src.toLong()
            } else if (src is Double) {
                return src.toLong()
            } else if (src is BigDecimal) {
                return src.toLong()
            } else if (src is Float) {
                return src.toLong()
            } else if (src is String) {
                return java.lang.Long.valueOf(src)
            }
            throw MappingException(
                "can not map a ${src.javaClass} to ${Long::class.javaObjectType.name}",
            )
        }
    }

    private class DoubleReader : JsonReaderI<Double>(null) {
        override fun convert(src: Any?): Double? {
            if (src == null) {
                return null
            }
            if (src is Double) {
                return src
            } else if (src is Int) {
                return src.toDouble()
            } else if (src is Long) {
                return src.toDouble()
            } else if (src is BigDecimal) {
                return src.toDouble()
            } else if (src is Float) {
                return src.toDouble()
            } else if (src is String) {
                return java.lang.Double.valueOf(src)
            }
            throw MappingException(
                "can not map a ${src.javaClass} to ${Double::class.javaObjectType.name}",
            )
        }
    }

    private class FloatReader : JsonReaderI<Float>(null) {
        override fun convert(src: Any?): Float? {
            if (src == null) {
                return null
            }
            if (src is Float) {
                return src
            } else if (src is Int) {
                return src.toFloat()
            } else if (src is Long) {
                return src.toFloat()
            } else if (src is BigDecimal) {
                return src.toFloat()
            } else if (src is Double) {
                return src.toFloat()
            } else if (src is String) {
                return java.lang.Float.valueOf(src)
            }
            throw MappingException(
                "can not map a ${src.javaClass} to ${Float::class.javaObjectType.name}",
            )
        }
    }

    private class BigDecimalReader : JsonReaderI<BigDecimal>(null) {
        override fun convert(src: Any?): BigDecimal? {
            if (src == null) {
                return null
            }
            return BigDecimal(src.toString())
        }
    }

    private class BigIntegerReader : JsonReaderI<BigInteger>(null) {
        override fun convert(src: Any?): BigInteger? {
            if (src == null) {
                return null
            }
            return BigInteger(src.toString())
        }
    }

    private class DateReader : JsonReaderI<Date>(null) {
        override fun convert(src: Any?): Date? {
            if (src == null) {
                return null
            }
            if (src is Date) {
                return src
            } else if (src is Long) {
                return Date(src)
            } else if (src is String) {
                try {
                    return DateFormat.getInstance().parse(src)
                } catch (exception: ParseException) {
                    throw MappingException(exception)
                }
            }
            throw MappingException(
                "can not map a ${src.javaClass} to ${Date::class.java.name}",
            )
        }
    }

    private class BooleanReader : JsonReaderI<Boolean>(null) {
        override fun convert(src: Any?): Boolean? {
            if (src == null) {
                return null
            }
            if (src is Boolean) {
                return src
            }
            throw MappingException(
                "can not map a ${src.javaClass} to ${Boolean::class.javaObjectType.name}",
            )
        }
    }

    private companion object {
        @Suppress("ktlint:standard:property-naming")
        private var DEFAULT =
            JsonReader().apply {
                registerReader(Long::class.javaObjectType, LongReader())
                registerReader(java.lang.Long.TYPE, LongReader())
                registerReader(Int::class.javaObjectType, IntegerReader())
                registerReader(java.lang.Integer.TYPE, IntegerReader())
                registerReader(Double::class.javaObjectType, DoubleReader())
                registerReader(java.lang.Double.TYPE, DoubleReader())
                registerReader(Float::class.javaObjectType, FloatReader())
                registerReader(java.lang.Float.TYPE, FloatReader())
                registerReader(BigDecimal::class.java, BigDecimalReader())
                registerReader(String::class.java, StringReader())
                registerReader(Date::class.java, DateReader())
                registerReader(BigInteger::class.java, BigIntegerReader())
                registerReader(java.lang.Boolean.TYPE, BooleanReader())
            }
    }
}
