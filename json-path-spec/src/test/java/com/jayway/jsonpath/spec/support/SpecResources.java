package com.jayway.jsonpath.spec.support;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

public final class SpecResources {

    private SpecResources() {}

    public static String read(String resourcePath) {
        try (InputStream input = SpecResources.class.getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IllegalArgumentException("Classpath resource not found: " + resourcePath);
            }
            return new String(input.readAllBytes(), UTF_8);
        } catch (IOException exception) {
            throw new UncheckedIOException(
                    "Could not read classpath resource: " + resourcePath, exception);
        }
    }
}
