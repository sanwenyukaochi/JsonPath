package com.jayway.jsonpath.spi.json;

import com.jayway.jsonpath.internal.spi.json.JsonSmartJsonProvider;

public abstract class JsonProviderFactory {

    private static JsonProvider provider = new JsonSmartJsonProvider();

    public static JsonProvider createProvider() {
        return provider;
    }

    public static synchronized void setProvider(JsonProvider jsonProvider) {
        provider = jsonProvider;
    }
}
