package com.jayway.jsonpath;

import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.MethodSource;

@ParameterizedClass
@MethodSource("configurations")
public class JsonProviderTestObjectMapping extends BaseTest {

    private static final String JSON =
"""
[{
   "foo" : "foo0",
   "bar" : 0,
   "baz" : true,
   "gen" : {"prop" : "yepp0"}},{
   "foo" : "foo1",
   "bar" : 1,
   "baz" : true,
   "gen" : {"prop" : "yepp1"}},{
   "foo" : "foo2",
   "bar" : 2,
   "baz" : true,
   "gen" : {"prop" : "yepp2"}}]\
""";

    private final Configuration conf;

    public JsonProviderTestObjectMapping(Configuration conf) {
        this.conf = conf;
    }

    public static Iterable<Configuration> configurations() {
        return Configurations.objectMappingConfigurations();
    }

    @Test
    public void list_of_numbers() {

        TypeRef<List<Double>> typeRef = new TypeRef<List<Double>>() {};

        assertThat(using(conf).parse(JSON_DOCUMENT).read("$.store.book[*].display-price", typeRef))
                .containsExactly(8.95D, 12.99D, 8.99D, 22.99D);
    }

    @Test
    public void test_type_ref() throws IOException {
        TypeRef<List<FooBarBaz<Sub>>> typeRef = new TypeRef<List<FooBarBaz<Sub>>>() {};

        assertThat(using(conf).parse(JSON).read("$", typeRef))
                .extracting("foo")
                .containsExactly("foo0", "foo1", "foo2");
    }

    public static class FooBarBaz<T> {
        public T gen;
        public String foo;
        public Long bar;
        public boolean baz;
    }

    public static class Sub {
        public String prop;
    }
}
