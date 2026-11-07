package com.jayway.jsonpath.spi;

import com.jayway.jsonpath.spi.impl.JsonSmartJsonProvider;

public abstract class JsonProviderFactory {

    public static JsonProviderFactory factory =
            new JsonProviderFactory() {
                @Override
                protected JsonProvider create() {
                    return new JsonSmartJsonProvider();
                }
            };

    public static JsonProvider createProvider() {
        return factory.create();
    }

    protected abstract JsonProvider create();
}
