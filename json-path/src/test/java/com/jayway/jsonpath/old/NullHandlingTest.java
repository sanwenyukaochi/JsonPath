package com.jayway.jsonpath.old;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class NullHandlingTest {

    public static final String DOCUMENT =
"""
{
   "root-property": "root-property-value",
   "root-property-null": null,
   "children": [
      {
         "id": 0,
         "name": "name-0",
         "age": 0
      },
      {
         "id": 1,
         "name": "name-1",
         "age": null      },
      {
         "id": 3,
         "name": "name-3"
      }
   ]
}\
""";

    @Test
    public void not_defined_property_throws_PathNotFoundException() {
        assertThrows(
                PathNotFoundException.class,
                () -> {
                    JsonPath.read(DOCUMENT, "$.children[0].child.age");
                });
    }

    @Test
    public void last_token_defaults_to_null() {
        Configuration configuration =
                Configuration.builder().options(Option.DEFAULT_PATH_LEAF_TO_NULL).build();

        assertNull(JsonPath.parse(DOCUMENT, configuration).read("$.children[2].age"));
    }

    @Test
    public void null_property_returns_null() {
        Integer age = JsonPath.read(DOCUMENT, "$.children[1].age");
        assertEquals(null, age);
    }

    @Test
    public void the_age_of_all_with_age_defined() {
        List<Integer> result =
                JsonPath.using(
                                Configuration.defaultConfiguration()
                                        .options(Option.SUPPRESS_EXCEPTIONS))
                        .parse(DOCUMENT)
                        .read("$.children[*].age");

        Assertions.assertThat(result).containsSequence(0, null);
    }

    @Test
    public void path2() {
        List<Object> result =
                JsonPath.read("{\"a\":[{\"b\":1,\"c\":2},{\"b\":5,\"c\":2}]}", "a[?(@.b==4)].c");
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    public void path() {
        String json = "{\"a\":[{\"b\":1,\"c\":2},{\"b\":5,\"c\":2}]}";

        List<Object> result =
                JsonPath.using(
                                Configuration.defaultConfiguration()
                                        .options(Option.DEFAULT_PATH_LEAF_TO_NULL))
                        .parse(json)
                        .read("a[?(@.b==5)].d");

        System.out.println(result);
    }
}
