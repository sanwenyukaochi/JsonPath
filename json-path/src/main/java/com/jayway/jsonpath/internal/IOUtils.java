package com.jayway.jsonpath.internal;

import java.io.Closeable;
import java.io.IOException;

public abstract class IOUtils {

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ignore) {
        }
    }
}
