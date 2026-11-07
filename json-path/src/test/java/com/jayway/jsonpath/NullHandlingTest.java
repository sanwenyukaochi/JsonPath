package com.jayway.jsonpath;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.hamcrest.Matchers;
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

        assertNull(JsonPath.read(DOCUMENT, "$.children[2].age"));
    }

    @Test
    public void null_property_returns_null() {
        Integer age = JsonPath.read(DOCUMENT, "$.children[1].age");
        assertEquals(null, age);
    }

    @Test
    public void the_age_of_all_with_age_defined() {
        List<Integer> result = JsonPath.read(DOCUMENT, "$.children[*].age");

        assertThat(result, Matchers.hasItems(0, null));
    }

    @Test
    public void path2() {
        System.out.println(
                (Object)
                        JsonPath.read(
                                "{\"a\":[{\"b\":1,\"c\":2},{\"b\":5,\"c\":2}]}", "a[?(@.b==4)].c"));
    }

    public void path() {
        System.out.println(
                (Object)
                        JsonPath.read(
                                "{\"a\":[{\"b\":1,\"c\":2},{\"b\":5,\"c\":2}]}", "a[?(@.b==5)].d"));
    }
}
