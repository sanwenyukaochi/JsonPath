package com.jayway.jsonpath.old.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyPathTokenTest {

    private static final Logger logger = LoggerFactory.getLogger(PropertyPathTokenTest.class);

    private String SIMPLE_MAP =
"""
{
   "foo" : "foo-val",
   "bar" : "bar-val",
   "baz" : {"baz-child" : "baz-child-val"}
}\
""";

    private String SIMPLE_ARRAY =
"""
[{
   "foo" : "foo-val-0",
   "bar" : "bar-val-0",
   "baz" : {"baz-child" : "baz-child-val"}
},{
   "foo" : "foo-val-1",
   "bar" : "bar-val-1",
   "baz" : {"baz-child" : "baz-child-val"}
}]\
""";

    @Test
    public void property_not_found() {

        Configuration configuration =
                Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS).build();

        String json = "{\"a\":{\"b\":1,\"c\":2}}";
        assertNull(JsonPath.parse(SIMPLE_MAP, configuration).read("$.not-found"));
    }

    @Test
    public void property_not_found_deep() {
        assertThrows(
                PathNotFoundException.class,
                () -> {
                    String result = JsonPath.read(SIMPLE_MAP, "$.foo.not-found");

                    assertThat(result).isNull();
                });
    }

    @Test
    public void property_not_found_option_throw() {
        assertThrows(
                PathNotFoundException.class,
                () -> {
                    String result =
                            JsonPath.using(Configuration.defaultConfiguration())
                                    .parse(SIMPLE_MAP)
                                    .read("$.not-found");

                    assertThat(result).isNull();
                });
    }

    @Test
    public void map_value_can_be_read_from_map() {

        String result = JsonPath.read(SIMPLE_MAP, "$.foo");

        assertThat(result).isEqualTo("foo-val");
    }

    @Test
    public void map_value_can_be_read_from_array() {

        List<String> result = JsonPath.read(SIMPLE_ARRAY, "$[*].foo");

        assertThat(result).containsOnly("foo-val-0", "foo-val-1");
    }

    @Test
    public void map_value_can_be_read_from_child_map() {

        String result = JsonPath.read(SIMPLE_MAP, "$.baz.baz-child");

        assertThat(result).isEqualTo("baz-child-val");
    }
}
