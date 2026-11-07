package com.jayway.jsonpath.internal;

import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.ValueCompareException;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public final class Utils {

    public static final String CR = System.getProperty("line.separator");
    private static final char BACKSLASH = '\\';

    public static List<Integer> createRange(int start, int end) {
        if (end <= start) {
            throw new IllegalArgumentException(
                    "Cannot create range from "
                            + start
                            + " to "
                            + end
                            + ", end must be greater than start.");
        }
        if (start == end - 1) {
            return Collections.emptyList();
        }
        List<Integer> range = new ArrayList<Integer>(end - start - 1);
        for (int i = start; i < end; i++) {
            range.add(i);
        }
        return range;
    }

    public static <T> Iterable<T> reverse(final List<T> list) {
        return new ListReverser<T>(list);
    }

    public static String join(String delimiter, String wrap, Iterable<? extends Object> objs) {
        Iterator<? extends Object> iter = objs.iterator();
        if (!iter.hasNext()) {
            return "";
        }
        StringBuilder buffer = new StringBuilder();
        buffer.append(wrap).append(iter.next()).append(wrap);
        while (iter.hasNext()) {
            buffer.append(delimiter).append(wrap).append(iter.next()).append(wrap);
        }
        return buffer.toString();
    }

    public static String join(String delimiter, Iterable<? extends Object> objs) {
        return join(delimiter, "", objs);
    }

    public static String concat(CharSequence... strings) {
        if (strings.length == 0) {
            return "";
        }
        if (strings.length == 1) {
            return strings[0].toString();
        }
        int length = 0;

        int indexOfSingleNonEmptyString = -1;
        for (int i = 0; i < strings.length; i++) {
            CharSequence charSequence = strings[i];
            int len = charSequence.length();
            length += len;
            if (indexOfSingleNonEmptyString != -2 && len > 0) {
                if (indexOfSingleNonEmptyString == -1) {
                    indexOfSingleNonEmptyString = i;
                } else {
                    indexOfSingleNonEmptyString = -2;
                }
            }
        }
        if (length == 0) {
            return "";
        }
        if (indexOfSingleNonEmptyString > 0) {
            return strings[indexOfSingleNonEmptyString].toString();
        }
        StringBuilder sb = new StringBuilder(length);
        for (CharSequence charSequence : strings) {
            sb.append(charSequence);
        }
        return sb.toString();
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ignore) {
        }
    }

    public static boolean isInt(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNumeric(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(str.charAt(i)) == false && !(str.charAt(i) == '.')) {
                return false;
            }
        }
        return true;
    }

    public static String unescape(String s) {
        if (s.indexOf(BACKSLASH) == -1) {
            return s;
        }
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == BACKSLASH) {
                char c2 = s.charAt(++i);
                switch (c2) {
                    case '\\':
                        c2 = '\\';
                        break;
                    case 'b':
                        c2 = '\b';
                        break;
                    case 'f':
                        c2 = '\f';
                        break;
                    case 'n':
                        c2 = '\n';
                        break;
                    case 'r':
                        c2 = '\r';
                        break;
                    case 't':
                        c2 = '\t';
                        break;
                    case 'u':
                        try {
                            String hex = s.substring(i + 1, i + 5);
                            c2 = (char) Integer.parseInt(hex, 16);
                            i += 4;
                        } catch (Exception e) {
                            throw new ValueCompareException("\\u parse failed", e);
                        }
                        break;
                }
                sb.append(c2);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
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

    public static void onlyOneIsTrue(final String message, final boolean... expressions) {
        if (!onlyOneIsTrueNonThrow(expressions)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static boolean onlyOneIsTrueNonThrow(final boolean... expressions) {
        int count = 0;
        for (final boolean expression : expressions) {
            if (expression && ++count > 1) {
                return false;
            }
        }
        return 1 == count;
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

    public static String toString(Object o) {
        if (null == o) {
            return null;
        }

        return o.toString();
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
            throw new JsonPathException(ex);
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
            throw new JsonPathException(ex);
        } catch (IOException ex) {
            throw new JsonPathException(ex);
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

    private static class ListReverser<T> implements Iterable<T> {
        private ListIterator<T> listIterator;

        public ListReverser(List<T> wrappedList) {
            this.listIterator = wrappedList.listIterator(wrappedList.size());
        }

        public Iterator<T> iterator() {
            return new Iterator<T>() {
                public boolean hasNext() {
                    return listIterator.hasPrevious();
                }

                public T next() {
                    return listIterator.previous();
                }

                public void remove() {
                    listIterator.remove();
                }
            };
        }
    }

    private Utils() {}
}
