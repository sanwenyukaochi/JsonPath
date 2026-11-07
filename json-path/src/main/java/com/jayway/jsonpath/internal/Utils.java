package com.jayway.jsonpath.internal;

import com.jayway.jsonpath.InvalidConversionException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Serializable;

public class Utils {

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ignore) {
        }
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    static int indexOf(CharSequence cs, CharSequence searchChar, int start) {
        return cs.toString().indexOf(searchChar.toString(), start);
    }

    public static int countMatches(CharSequence str, CharSequence sub) {
        if (isEmpty(str) || isEmpty(sub)) {
            return 0;
        }
        int count = 0;
        int idx = 0;
        while ((idx = indexOf(str, sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }

    public static <T> T notNull(T object, String message, Object... values) {
        if (object == null) {
            throw new IllegalArgumentException(String.format(message, values));
        }
        return object;
    }

    public static void isTrue(boolean expression, String message) {
        if (expression == false) {
            throw new IllegalArgumentException(message);
        }
    }

    public static <T extends CharSequence> T notEmpty(T chars, String message, Object... values) {
        if (chars == null) {
            throw new IllegalArgumentException(String.format(message, values));
        }
        if (chars.length() == 0) {
            throw new IllegalArgumentException(String.format(message, values));
        }
        return chars;
    }

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

    public static <T extends Serializable> T clone(T object) {
        if (object == null) {
            return null;
        }
        byte[] objectData = serialize(object);
        ByteArrayInputStream bais = new ByteArrayInputStream(objectData);

        ClassLoaderAwareObjectInputStream in = null;
        try {

            in = new ClassLoaderAwareObjectInputStream(bais, object.getClass().getClassLoader());

            @SuppressWarnings("unchecked")
            T readObject = (T) in.readObject();
            return readObject;

        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(
                    "ClassNotFoundException while reading cloned object data", ex);
        } catch (IOException ex) {
            throw new RuntimeException("IOException while reading cloned object data", ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                throw new RuntimeException(
                        "IOException on closing cloned object data InputStream.", ex);
            }
        }
    }

    public static void serialize(Serializable obj, OutputStream outputStream) {
        if (outputStream == null) {
            throw new IllegalArgumentException("The OutputStream must not be null");
        }
        ObjectOutputStream out = null;
        try {

            out = new ObjectOutputStream(outputStream);
            out.writeObject(obj);

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {

            }
        }
    }

    public static byte[] serialize(Serializable obj) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
        serialize(obj, baos);
        return baos.toByteArray();
    }

    public static Object deserialize(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("The InputStream must not be null");
        }
        ObjectInputStream in = null;
        try {

            in = new ObjectInputStream(inputStream);
            return in.readObject();

        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {

            }
        }
    }

    public static Object deserialize(byte[] objectData) {
        if (objectData == null) {
            throw new IllegalArgumentException("The byte[] must not be null");
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(objectData);
        return deserialize(bais);
    }

    static class ClassLoaderAwareObjectInputStream extends ObjectInputStream {
        private ClassLoader classLoader;

        public ClassLoaderAwareObjectInputStream(InputStream in, ClassLoader classLoader)
                throws IOException {
            super(in);
            this.classLoader = classLoader;
        }

        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc)
                throws IOException, ClassNotFoundException {
            String name = desc.getName();
            try {
                return Class.forName(name, false, classLoader);
            } catch (ClassNotFoundException ex) {
                return Class.forName(name, false, Thread.currentThread().getContextClassLoader());
            }
        }
    }
}
