package com.jayway.jsonpath.spi;

import com.jayway.jsonpath.spi.impl.JacksonProvider;

public class MappingProviderFactory {

    private static MappingProvider mappingProvider;

    static {
        try {
            Class.forName("tools.jackson.databind.ObjectMapper");

            mappingProvider = new JacksonProvider();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                    "tools.jackson.databind.ObjectMapper not found on classpath. This is an optional dependency needed for POJO conversions.",
                    e);
        }
    }

    public static MappingProvider getInstance() {
        return mappingProvider;
    }
}
