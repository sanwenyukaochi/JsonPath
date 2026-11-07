package com.jayway.jsonpath;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import org.junit.jupiter.api.Test;

public class JacksonTest extends BaseTest {

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

        FooBarBaz fooBarBaz =
                JsonPath.using(JACKSON_CONFIGURATION).parse(json).read("$", FooBarBaz.class);

        assertThat(fooBarBaz.foo).isEqualTo("foo");
        assertThat(fooBarBaz.bar).isEqualTo(10L);
        assertThat(fooBarBaz.baz).isEqualTo(true);
    }

    public static class FooBarBaz {
        public String foo;
        public Long bar;
        public boolean baz;
    }

    @Test
    public void jackson_converts_dates() {

        Date now = new Date();

        Object json = singletonMap("date_as_long", now.getTime());

        Date date =
                JsonPath.using(JACKSON_CONFIGURATION)
                        .parse(json)
                        .read("$['date_as_long']", Date.class);

        assertThat(date).isEqualTo(now);
    }
}
