package com.jayway.jsonpath.spi.json;

public abstract class JsonProviderFactory {

    private static JsonProvider provider = null;

    private static final String DEFAULT_JSON_PROVIDER =
            "com.jayway.jsonpath.internal.spi.json.JsonSmartJsonProvider";

    public static JsonProvider createProvider() {

        if (provider == null) {
            synchronized (JsonProviderFactory.class) {
                if (provider == null) {
                    try {
                        provider =
                                (JsonProvider) Class.forName(DEFAULT_JSON_PROVIDER).newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to create JsonProvider", e);
                    }
                }
            }
        }
        return provider;
    }

    public static synchronized void setProvider(JsonProvider jsonProvider) {
        provider = jsonProvider;
    }
}
