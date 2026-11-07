package com.jayway.jsonpath.spi;

import com.jayway.jsonpath.spi.impl.JsonSmartJsonProvider;

public abstract class JsonProviderFactory {

    private static Class defaultProvider = null;

    public static JsonProviderFactory factory =
            new JsonProviderFactory() {
                @Override
                protected JsonProvider create() {
                    JsonProvider provider = null;
                    try {
                        if (defaultProvider != null) {
                            provider = (JsonProvider) defaultProvider.newInstance();
                        }
                    } catch (Throwable t) {
                    }
                    if (provider == null) {
                        provider = new JsonSmartJsonProvider();
                    }
                    return provider;
                }
            };

    public static JsonProvider createProvider() {
        return factory.create();
    }

    public static synchronized void setDefaultProvider(Class<? extends JsonProvider> jsonProvider) {
        defaultProvider = jsonProvider;
    }

    protected abstract JsonProvider create();
}
