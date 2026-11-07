package com.jayway.jsonpath;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jayway.jsonpath.internal.Utils;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import net.minidev.json.JSONObject;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

public class IssuesTest {

    @Test
    public void issue_36() {

        String json =
                """
                {
                    "arrayOfObjectsAndArrays": [
                        {"k": ["json"]},
                        {"k": ["path"]},
                        {"k": ["is"]},
                        {"k": ["cool"]}
                    ],
                    "arrayOfObjects": [
                        {"k": "json"},
                        {"k": "path"},
                        {"k": "is"},
                        {"k": "cool"}
                    ]
                }\
                """;

        Object o1 = JsonPath.read(json, "$.arrayOfObjectsAndArrays..k ");
        Object o2 = JsonPath.read(json, "$.arrayOfObjects..k ");

        assertEquals("[[\"json\"],[\"path\"],[\"is\"],[\"cool\"]]", o1.toString());
        assertEquals("[\"json\",\"path\",\"is\",\"cool\"]", o2.toString());
    }

    @Test
    public void issue_11() throws Exception {
        assertThrows(
                PathNotFoundException.class,
                () -> {
                    String json =
                            """
                            { "foo" : [] }\
                            """;
                    List<String> result = JsonPath.read(json, "$.foo[?(@.rel= 'item')][0].uri");

                    System.out.println(
                            JsonPath.compile("$.foo[?(@.rel= 'item')][0].uri").isPathDefinite());
                    System.out.println(
                            JsonPath.compile("$.foo[?(@.rel= 'item')][0]").isPathDefinite());
                    System.out.println(
                            JsonPath.compile("$.foo[?(@.rel= 'item')]").isPathDefinite());

                    assertTrue(result.isEmpty());
                });
    }

    @Test
    public void issue_15() throws Exception {
        String json =
                """
                { "store": {
                    "book": [
                      { "category": "reference",
                        "author": "Nigel Rees",
                        "title": "Sayings of the Century",
                        "price": 8.95
                      },
                      { "category": "fiction",
                        "author": "Herman Melville",
                        "title": "Moby Dick",
                        "isbn": "0-553-21311-3",
                        "price": 8.99,
                        "retailer": null,
                        "children": true,
                        "number": -2.99
                      },
                      { "category": "fiction",
                        "author": "J. R. R. Tolkien",
                        "title": "The Lord of the Rings",
                        "isbn": "0-395-19395-8",
                        "price": 22.99,
                        "number":0,
                        "children": false
                      }
                    ]
                  }
                }\
                """;

        List<String> titles = JsonPath.read(json, "$.store.book[?(@.children==true)].title");

        assertThat(titles, Matchers.contains("Moby Dick"));
        assertEquals(1, titles.size());
    }

    @Test
    public void issue_24() {

        InputStream is = null;
        try {
            is = this.getClass().getResourceAsStream("/issue_24.json");

            Object o = JsonPath.read(is, "$.project.field[*].@key");

            System.out.println(o);

            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.closeQuietly(is);
        }
    }

    @Test
    public void issue_28_string() {
        String json = "{\"contents\": [\"one\",\"two\",\"three\"]}";

        List<String> result = JsonPath.read(json, "$.contents[?(@  == 'two')]");

        assertThat(result, Matchers.contains("two"));
        assertEquals(1, result.size());
    }

    @Test
    public void issue_28_int() {
        String json = "{\"contents\": [1,2,3]}";

        List<Integer> result = JsonPath.read(json, "$.contents[?(@ == 2)]");

        assertThat(result, Matchers.contains(2));
        assertEquals(1, result.size());
    }

    @Test
    public void issue_28_boolean() {
        String json = "{\"contents\": [true, true, false]}";

        List<Boolean> result = JsonPath.read(json, "$.contents[?(@  == true)]");

        assertThat(result, Matchers.contains(true, true));
        assertEquals(2, result.size());
    }

    @Test
    public void issue_22() throws Exception {
        assertThrows(
                PathNotFoundException.class,
                () -> {
                    Configuration configuration =
                            Configuration.builder()
                                    .options(Option.THROW_ON_MISSING_PROPERTY)
                                    .build();
                    String json = "{\"a\":{\"b\":1,\"c\":2}}";
                    System.out.println((Object) JsonPath.parse(json, configuration).read("a.d"));
                });
    }

    @Test
    public void issue_22c() throws Exception {
        Configuration configuration = Configuration.builder().build();

        String json = "{\"a\":{\"b\":1,\"c\":2}}";
        assertNull(JsonPath.parse(json, configuration).read("a.d"));
    }

    @Test
    public void issue_22b() throws Exception {
        String json = "{\"a\":[{\"b\":1,\"c\":2},{\"b\":5,\"c\":2}]}";
        System.out.println((Object) JsonPath.read(json, "a[?(@.b==5)].d"));
        System.out.println((Object) JsonPath.read(json, "a[?(@.b==5)].d"));
    }

    @Test
    public void issue_26() throws Exception {
        assertThrows(
                PathNotFoundException.class,
                () -> {
                    String json = "[{\"a\":[{\"b\":1,\"c\":2}]}]";
                    Object o = JsonPath.read(json, "$.a");
                });
    }

    @Test
    public void issue_29_a() throws Exception {
        String json =
                """
                {
                    "list": [
                        {
                            "a": "atext",
                            "b.b-a": "batext2",
                            "b": {
                                "b-a": "batext",
                                "b-b": "bbtext"
                            }
                        },
                        {
                            "a": "atext2",
                            "b": {
                                "b-a": "batext2",
                                "b-b": "bbtext2"
                            }
                        }
                    ]
                }\
                """;

        List<JSONObject> result = JsonPath.read(json, "$.list[?(@['b.b-a']=='batext2')]");
        assertEquals(1, result.size());
        assertEquals("atext", result.get(0).get("a"));

        result = JsonPath.read(json, "$.list[?(@.b.b-a=='batext2')]");
        assertEquals(1, result.size());
        assertEquals("atext2", result.get(0).get("a"));
    }

    @Test
    public void issue_29_b() throws Exception {
        String json =
                """
                {
                    "list": [
                        {
                            "a": "atext",
                            "b": {
                                "b-a": "batext",
                                "b-b": "bbtext"
                            }
                        },
                        {
                            "a": "atext2",
                            "b": {
                                "b-a": "batext2",
                                "b-b": "bbtext2"
                            }
                        }
                    ]
                }\
                """;
        List<String> result =
                JsonPath.read(
                        json, "$.list[?]", Filter.filter(Criteria.where("b.b-a").eq("batext2")));

        assertTrue(result.size() == 1);
    }

    @Test
    public void issue_30() throws Exception {
        String json = "{\"foo\" : {\"@id\" : \"123\", \"$\" : \"hello\"}}";

        assertEquals("123", JsonPath.read(json, "foo.@id"));
        assertEquals("hello", JsonPath.read(json, "foo.$"));
    }

    @Test
    public void issue_32() {
        String json = "{\"text\" : \"skill: \\\"Heuristic Evaluation\\\"\", \"country\" : \"\"}";
        assertEquals("skill: \"Heuristic Evaluation\"", JsonPath.read(json, "$.text"));
    }

    @Test
    public void issue_33() {
        String json =
                """
                { "store": {
                    "book": [
                      { "category": "reference",
                        "author": {
                          "name": "Author Name",
                          "age": 36
                        },
                        "title": "Sayings of the Century",
                        "price": 8.95
                      },
                      { "category": "fiction",
                        "author": "Evelyn Waugh",
                        "title": "Sword of Honour",
                        "price": 12.99,
                        "isbn": "0-553-21311-3"
                      }
                    ],
                    "bicycle": {
                      "color": "red",
                      "price": 19.95
                    }
                  }
                }\
                """;

        List<Map<String, Object>> result =
                JsonPath.read(json, "$.store.book[?(@.author.age == 36)]");

        System.out.println(result);
    }
}
