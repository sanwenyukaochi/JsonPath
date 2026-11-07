package com.jayway.jsonpath;

import static com.jayway.jsonpath.JsonPath.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

public class JsonSmartMappingProviderTest {

    private static final String JSON =
"""
[{
   "foo" : "foo0",
   "bar" : 0,
   "baz" : true,
   "gen" : {"eric" : "yepp"}},{
   "foo" : "foo1",
   "bar" : 1,
   "baz" : true,
   "gen" : {"eric" : "yepp"}},{
   "foo" : "foo2",
   "bar" : 2,
   "baz" : true,
   "gen" : {"eric" : "yepp"}}]\
""";

    @Test
    public void class_mapping() {

        Object map = Configuration.defaultConfiguration().jsonProvider().createMap();
        Configuration.defaultConfiguration().jsonProvider().setProperty(map, "eric", "eric-val");

        Gen gen = parse(map).read("$", Gen.class);

        assertThat(gen.eric).isEqualTo("eric-val");
    }

    @Test
    public void test_type_ref() throws IOException {
        assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    TypeRef<List<FooBarBaz>> typeRef = new TypeRef<List<FooBarBaz>>() {};

                    List gen = parse(JSON).read("$", typeRef);
                });
    }

    public static class FooBarBaz {
        public Gen gen;
        public String foo;
        public Long bar;
        public boolean baz;
    }

    public static class Gen {
        public String eric;
    }
}
