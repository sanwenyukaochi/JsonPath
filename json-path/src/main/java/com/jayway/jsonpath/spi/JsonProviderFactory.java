package com.jayway.jsonpath.spi;

import com.jayway.jsonpath.spi.impl.JsonSmartJsonProvider;

public abstract class JsonProviderFactory {

    private static JsonProvider provider = new JsonSmartJsonProvider();

    public static JsonProvider createProvider() {
        return provider;
    }

    public static synchronized void setProvider(JsonProvider jsonProvider) {
        provider = jsonProvider;
    }
}
