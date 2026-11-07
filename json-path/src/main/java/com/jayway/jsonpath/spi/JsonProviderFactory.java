package com.jayway.jsonpath.spi;

import com.jayway.jsonpath.spi.impl.JsonSmartJsonProvider;

public abstract class JsonProviderFactory {

    public static JsonProvider getInstance() {
        return new JsonSmartJsonProvider();
    }
}
