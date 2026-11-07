package com.jayway.jsonpath;

import static org.junit.jupiter.api.Assertions.assertNull;

import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import org.junit.jupiter.api.Test;

public class TestSuppressExceptions {

    @Test
    public void testSuppressExceptionsIsRespected() {
        ParseContext parseContext =
                JsonPath.using(
                        new Configuration.ConfigurationBuilder()
                                .jsonProvider(new JacksonJsonProvider())
                                .mappingProvider(new JacksonMappingProvider())
                                .options(Option.SUPPRESS_EXCEPTIONS)
                                .build());
        String json = "{}";
        assertNull(parseContext.parse(json).read(JsonPath.compile("$.missing")));
    }

    @Test
    public void testSuppressExceptionsIsRespectedPath() {
        ParseContext parseContext =
                JsonPath.using(
                        new Configuration.ConfigurationBuilder()
                                .jsonProvider(new JacksonJsonProvider())
                                .mappingProvider(new JacksonMappingProvider())
                                .options(Option.SUPPRESS_EXCEPTIONS, Option.AS_PATH_LIST)
                                .build());
        String json = "{}";
        assertNull(parseContext.parse(json).read(JsonPath.compile("$.missing")));
    }
}
