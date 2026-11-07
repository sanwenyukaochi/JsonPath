package com.jayway.jsonpath.internal;

import com.jayway.jsonpath.InvalidConversionException;

public class ConvertUtils {

    public static Integer toInt(Object o) {
        if (null == o) return null;
        if (o instanceof Number) return ((Number) o).intValue();
        try {
            return Integer.valueOf(o.toString().trim(), 10);
        } catch (Exception e) {
            throw new InvalidConversionException(
                    "Could not convert " + o.toString() + " to Integer");
        }
    }

    public static Long toLong(Object o) {
        if (null == o) return null;
        if (o instanceof Number) return ((Number) o).longValue();
        try {
            return Long.valueOf(o.toString().trim(), 10);
        } catch (Exception e) {
            throw new InvalidConversionException("Could not convert " + o.toString() + " to Long");
        }
    }

    public static Double toDouble(Object o) {
        if (null == o) return null;
        if (o instanceof Number) return ((Number) o).doubleValue();
        try {
            return Double.valueOf(o.toString().trim());
        } catch (Exception e) {
            throw new InvalidConversionException(
                    "Could not convert " + o.toString() + " to Double");
        }
    }

    public static String toString(Object o) {
        if (null == o) {
            return null;
        }

        return o.toString();
    }
}
