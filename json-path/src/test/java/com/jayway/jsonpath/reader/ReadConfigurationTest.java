package com.jayway.jsonpath.reader;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;

public class ReadConfigurationTest {

    private static JsonProvider provider = JsonProviderFactory.createProvider();

    public void fluent() {

        Configuration configuration = Configuration.defaultConfiguration();

        Configuration configuration2 =
                Configuration.builder()
                        .jsonProvider(JsonProviderFactory.createProvider())
                        .options(Option.THROW_ON_MISSING_PROPERTY)
                        .build();

        JsonProvider jsonProvider = JsonProviderFactory.createProvider();

        JsonPath.using(configuration).parse("{}").read("$");
        JsonPath.using(jsonProvider).parse("{}").read("$");

        JsonPath.parse("{}").read("$");
        JsonPath.parse("{}", configuration).read("$");

        JsonPath.using(configuration).parse("{}").read("$");

        JsonPath.read(configuration, "{}", "$");
    }
}
