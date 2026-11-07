package com.jayway.jsonpath;

import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

public class JsonProviderTest extends BaseTest {

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

    @Test
    public void strings_are_unwrapped() {
        assertThat(
                        using(JACKSON_CONFIGURATION)
                                .parse(JSON_DOCUMENT)
                                .read("$.string-property", String.class))
                .isEqualTo("string-value");
        assertThat(
                        using(JACKSON_TREE_CONFIGURATION)
                                .parse(JSON_DOCUMENT)
                                .read("$.string-property", String.class))
                .isEqualTo("string-value");
        assertThat(
                        using(JSON_SMART_CONFIGURATION)
                                .parse(JSON_DOCUMENT)
                                .read("$.string-property", String.class))
                .isEqualTo("string-value");
        assertThat(
                        using(GSON_CONFIGURATION)
                                .parse(JSON_DOCUMENT)
                                .read("$.string-property", String.class))
                .isEqualTo("string-value");
    }

    @Test
    public void integers_are_unwrapped() {
        assertThat(
                        using(JACKSON_CONFIGURATION)
                                .parse(JSON_DOCUMENT)
                                .read("$.int-max-property", Integer.class))
                .isEqualTo(Integer.MAX_VALUE);
        assertThat(
                        using(JACKSON_TREE_CONFIGURATION)
                                .parse(JSON_DOCUMENT)
                                .read("$.int-max-property", Integer.class))
                .isEqualTo(Integer.MAX_VALUE);
        assertThat(
                        using(JSON_SMART_CONFIGURATION)
                                .parse(JSON_DOCUMENT)
                                .read("$.int-max-property", Integer.class))
                .isEqualTo(Integer.MAX_VALUE);
        assertThat(
                        using(GSON_CONFIGURATION)
                                .parse(JSON_DOCUMENT)
                                .read("$.int-max-property", Integer.class))
                .isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void ints_are_unwrapped() {
        assertThat(
                        using(JACKSON_CONFIGURATION)
                                .parse(JSON_DOCUMENT)
                                .read("$.int-max-property", int.class))
                .isEqualTo(Integer.MAX_VALUE);
        assertThat(
                        using(JACKSON_TREE_CONFIGURATION)
                                .parse(JSON_DOCUMENT)
                                .read("$.int-max-property", int.class))
                .isEqualTo(Integer.MAX_VALUE);
        assertThat(
                        using(JSON_SMART_CONFIGURATION)
                                .parse(JSON_DOCUMENT)
                                .read("$.int-max-property", int.class))
                .isEqualTo(Integer.MAX_VALUE);
        assertThat(
                        using(GSON_CONFIGURATION)
                                .parse(JSON_DOCUMENT)
                                .read("$.int-max-property", int.class))
                .isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void list_of_numbers() {

        TypeRef<List<Double>> typeRef = new TypeRef<List<Double>>() {};

        assertThat(
                        using(JACKSON_TREE_CONFIGURATION)
                                .parse(JSON_DOCUMENT)
                                .read("$.store.book[*].display-price", typeRef))
                .containsExactly(8.95D, 12.99D, 8.99D, 22.99D);
        assertThat(
                        using(JACKSON_CONFIGURATION)
                                .parse(JSON_DOCUMENT)
                                .read("$.store.book[*].display-price", typeRef))
                .containsExactly(8.95D, 12.99D, 8.99D, 22.99D);
        assertThat(
                        using(GSON_CONFIGURATION)
                                .parse(JSON_DOCUMENT)
                                .read("$.store.book[*].display-price", typeRef))
                .containsExactly(8.95D, 12.99D, 8.99D, 22.99D);
    }

    @Test
    public void test_type_ref() throws IOException {
        TypeRef<List<FooBarBaz<Sub>>> typeRef = new TypeRef<List<FooBarBaz<Sub>>>() {};

        assertThat(using(JACKSON_CONFIGURATION).parse(JSON).read("$", typeRef))
                .extracting("foo")
                .containsExactly("foo0", "foo1", "foo2");
        assertThat(using(JACKSON_TREE_CONFIGURATION).parse(JSON).read("$", typeRef))
                .extracting("foo")
                .containsExactly("foo0", "foo1", "foo2");
        assertThat(using(GSON_CONFIGURATION).parse(JSON).read("$", typeRef))
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
