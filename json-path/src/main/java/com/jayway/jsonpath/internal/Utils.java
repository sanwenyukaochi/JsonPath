package com.jayway.jsonpath.internal;

import com.jayway.jsonpath.JsonPathException;

import java.io.Closeable;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;

public final class Utils {

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

    public static String escape(String str, boolean escapeSingleQuote) {
        if (str == null) {
            return null;
        }
        int len = str.length();
        StringWriter writer = new StringWriter(len * 2);

        for (int i = 0; i < len; i++) {
            char ch = str.charAt(i);

            if (ch > 0xfff) {
                writer.write("\\u" + hex(ch));
            } else if (ch > 0xff) {
                writer.write("\\u0" + hex(ch));
            } else if (ch > 0x7f) {
                writer.write("\\u00" + hex(ch));
            } else if (ch < 32) {
                switch (ch) {
                    case '\b':
                        writer.write('\\');
                        writer.write('b');
                        break;
                    case '\n':
                        writer.write('\\');
                        writer.write('n');
                        break;
                    case '\t':
                        writer.write('\\');
                        writer.write('t');
                        break;
                    case '\f':
                        writer.write('\\');
                        writer.write('f');
                        break;
                    case '\r':
                        writer.write('\\');
                        writer.write('r');
                        break;
                    default:
                        if (ch > 0xf) {
                            writer.write("\\u00" + hex(ch));
                        } else {
                            writer.write("\\u000" + hex(ch));
                        }
                        break;
                }
            } else {
                switch (ch) {
                    case '\'':
                        if (escapeSingleQuote) {
                            writer.write('\\');
                        }
                        writer.write('\'');
                        break;
                    case '"':
                        writer.write('\\');
                        writer.write('"');
                        break;
                    case '\\':
                        writer.write('\\');
                        writer.write('\\');
                        break;
                    case '/':
                        writer.write('\\');
                        writer.write('/');
                        break;
                    default:
                        writer.write(ch);
                        break;
                }
            }
        }
        return writer.toString();
    }

    public static String unescape(String str) {
        if (str == null) {
            return null;
        }
        int len = str.length();
        StringWriter writer = new StringWriter(len);
        StringBuffer unicode = new StringBuffer(4);
        boolean hadSlash = false;
        boolean inUnicode = false;
        for (int i = 0; i < len; i++) {
            char ch = str.charAt(i);
            if (inUnicode) {
                unicode.append(ch);
                if (unicode.length() == 4) {
                    try {
                        int value = Integer.parseInt(unicode.toString(), 16);
                        writer.write((char) value);
                        unicode.setLength(0);
                        inUnicode = false;
                        hadSlash = false;
                    } catch (NumberFormatException nfe) {
                        throw new JsonPathException(
                                "Unable to parse unicode value: " + unicode, nfe);
                    }
                }
                continue;
            }
            if (hadSlash) {
                hadSlash = false;
                switch (ch) {
                    case '\\':
                        writer.write('\\');
                        break;
                    case '\'':
                        writer.write('\'');
                        break;
                    case '\"':
                        writer.write('"');
                        break;
                    case 'r':
                        writer.write('\r');
                        break;
                    case 'f':
                        writer.write('\f');
                        break;
                    case 't':
                        writer.write('\t');
                        break;
                    case 'n':
                        writer.write('\n');
                        break;
                    case 'b':
                        writer.write('\b');
                        break;
                    case 'u':
                        {
                            inUnicode = true;
                            break;
                        }
                    default:
                        writer.write(ch);
                        break;
                }
                continue;
            } else if (ch == '\\') {
                hadSlash = true;
                continue;
            }
            writer.write(ch);
        }
        if (hadSlash) {
            writer.write('\\');
        }
        return writer.toString();
    }

    public static String hex(char ch) {
        return Integer.toHexString(ch).toUpperCase();
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    static int indexOf(CharSequence cs, CharSequence searchChar, int start) {
        return cs.toString().indexOf(searchChar.toString(), start);
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

    private Utils() {}
}
