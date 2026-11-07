package com.jayway.jsonpath;

import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.internal.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.internal.spi.mapper.JacksonMappingProvider;
import org.junit.jupiter.api.Test;

public class JacksonTest {

    private static Configuration config =
            Configuration.builder()
                    .conversionProvider(new JacksonMappingProvider())
                    .jsonProvider(new JacksonJsonProvider())
                    .build();

    @Test
    public void an_object_can_be_mapped_to_pojo() {

        String json =
"""
{
    "foo": "foo",
    "bar": 10,
    "baz": true
}
""";

        FooBarBaz fooBarBaz = JsonPath.using(config).parse(json).read("$", FooBarBaz.class);

        assertThat(fooBarBaz.foo).isEqualTo("foo");
        assertThat(fooBarBaz.bar).isEqualTo(10L);
        assertThat(fooBarBaz.baz).isEqualTo(true);
    }

    public static class FooBarBaz {
        public String foo;
        public Long bar;
        public boolean baz;
    }
}
