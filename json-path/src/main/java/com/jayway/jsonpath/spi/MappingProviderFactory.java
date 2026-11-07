package com.jayway.jsonpath.spi;

import com.jayway.jsonpath.spi.impl.JacksonProvider;

public abstract class MappingProviderFactory {

    public static MappingProviderFactory factory =
            new MappingProviderFactory() {

                private MappingProvider provider = null;

                @Override
                protected MappingProvider create() {
                    if (this.provider == null) {
                        synchronized (MappingProviderFactory.class) {
                            try {
                                Class.forName("tools.jackson.databind.ObjectMapper");

                                provider = new JacksonProvider();
                                return provider;
                            } catch (ClassNotFoundException e) {
                                throw new RuntimeException(
                                        "tools.jackson.databind.ObjectMapper not found on classpath. This is an optional dependency needed for POJO conversions.",
                                        e);
                            }
                        }
                    } else {
                        return provider;
                    }
                }
            };

    protected abstract MappingProvider create();

    public static MappingProvider createProvider() {
        return factory.create();
    }
}
