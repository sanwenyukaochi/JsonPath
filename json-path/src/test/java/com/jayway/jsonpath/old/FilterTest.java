package com.jayway.jsonpath.old;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.InvalidCriteriaException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.json.JsonProviderFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

public class FilterTest {

    public static final String DOCUMENT =
"""
{ "store": {
    "book": [\s
      { "category": "reference",
        "author": "Nigel Rees",
        "title": "Sayings of the Century",
        "price": 8.95
      },
      { "category": "fiction",
        "author": "Evelyn Waugh",
        "title": "Sword of Honour",
        "price": 12.99
      },
      { "category": "fiction",
        "author": "Herman Melville",
        "title": "Moby Dick",
        "isbn": "0-553-21311-3",
        "price": 8.99
      },
      { "category": "fiction",
        "author": "J. R. R. Tolkien",
        "title": "The Lord of the Rings",
        "isbn": "0-395-19395-8",
        "price": 22.99
      }
    ],
    "bicycle": {
      "color": "red",
      "price": 19.95,
      "foo:bar": "fooBar",
      "dot.notation": "new"
    }
  }
}\
""";

    private static final JsonProvider jp = JsonProviderFactory.createProvider();

    private static final Configuration conf = Configuration.defaultConfiguration();

    @Test
    public void is_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("foo", "foo");
        check.put("bar", null);

        assertTrue(filter(where("bar").is(null)).apply(check, conf));
        assertTrue(filter(where("foo").is("foo")).apply(check, conf));
        assertFalse(filter(where("foo").is("xxx")).apply(check, conf));
        assertFalse(filter(where("bar").is("xxx")).apply(check, conf));
    }

    @Test
    public void ne_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("foo", "foo");
        check.put("bar", null);

        assertTrue(filter(where("foo").ne(null)).apply(check, conf));
        assertTrue(filter(where("foo").ne("not foo")).apply(check, conf));
        assertFalse(filter(where("foo").ne("foo")).apply(check, conf));
        assertFalse(filter(where("bar").ne(null)).apply(check, conf));
    }

    @Test
    public void gt_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("foo", 12.5D);
        check.put("foo_null", null);

        assertTrue(filter(where("foo").gt(12D)).apply(check, conf));
        assertFalse(filter(where("foo").gt(null)).apply(check, conf));
        assertFalse(filter(where("foo").gt(20D)).apply(check, conf));
        assertFalse(filter(where("foo_null").gt(20D)).apply(check, conf));
    }

    @Test
    public void gte_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("foo", 12.5D);
        check.put("foo_null", null);

        assertTrue(filter(where("foo").gte(12D)).apply(check, conf));
        assertTrue(filter(where("foo").gte(12.5D)).apply(check, conf));
        assertFalse(filter(where("foo").gte(null)).apply(check, conf));
        assertFalse(filter(where("foo").gte(20D)).apply(check, conf));
        assertFalse(filter(where("foo_null").gte(20D)).apply(check, conf));
    }

    @Test
    public void lt_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("foo", 10.5D);
        check.put("foo_null", null);

        assertFalse(filter(where("foo").lt(null)).apply(check, conf));
    }

    @Test
    public void lte_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("foo", 12.5D);
        check.put("foo_null", null);

        assertTrue(filter(where("foo").lte(13D)).apply(check, conf));
        assertFalse(filter(where("foo").lte(null)).apply(check, conf));
        assertFalse(filter(where("foo").lte(5D)).apply(check, conf));
        assertFalse(filter(where("foo_null").lte(5D)).apply(check, conf));
    }

    @Test
    public void in_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("item", 3);
        check.put("null_item", null);

        assertTrue(filter(where("item").in(1, 2, 3)).apply(check, conf));
        assertTrue(filter(where("item").in(asList(1, 2, 3))).apply(check, conf));
        assertFalse(filter(where("item").in(4, 5, 6)).apply(check, conf));
        assertFalse(filter(where("item").in(asList(4, 5, 6))).apply(check, conf));
        assertFalse(filter(where("item").in(asList('A'))).apply(check, conf));
        assertFalse(filter(where("item").in(asList((Object) null))).apply(check, conf));

        assertTrue(filter(where("null_item").in((Object) null)).apply(check, conf));
        assertFalse(filter(where("null_item").in(1, 2, 3)).apply(check, conf));
    }

    @Test
    public void nin_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("item", 3);
        check.put("null_item", null);

        assertTrue(filter(where("item").nin(4, 5)).apply(check, conf));
        assertTrue(filter(where("item").nin(asList(4, 5))).apply(check, conf));
        assertTrue(filter(where("item").nin(asList('A'))).apply(check, conf));
        assertTrue(filter(where("null_item").nin(1, 2, 3)).apply(check, conf));
        assertTrue(filter(where("item").nin(asList((Object) null))).apply(check, conf));

        assertFalse(filter(where("item").nin(3)).apply(check, conf));
        assertFalse(filter(where("item").nin(asList(3))).apply(check, conf));
    }

    @Test
    public void all_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("items", asList(1, 2, 3));

        assertTrue(filter(where("items").all(1, 2, 3)).apply(check, conf));
        assertFalse(filter(where("items").all(1, 2, 3, 4)).apply(check, conf));
    }

    @Test
    public void size_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("items", asList(1, 2, 3));
        check.put("items_empty", Collections.emptyList());

        assertTrue(filter(where("items").size(3)).apply(check, conf));
        assertTrue(filter(where("items_empty").size(0)).apply(check, conf));
        assertFalse(filter(where("items").size(2)).apply(check, conf));
    }

    @Test
    public void exists_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("foo", "foo");
        check.put("foo_null", null);

        assertTrue(filter(where("foo").exists(true)).apply(check, conf));
        assertFalse(filter(where("foo").exists(false)).apply(check, conf));

        assertTrue(filter(where("foo_null").exists(true)).apply(check, conf));
        assertFalse(filter(where("foo_null").exists(false)).apply(check, conf));

        assertTrue(filter(where("bar").exists(false)).apply(check, conf));
        assertFalse(filter(where("bar").exists(true)).apply(check, conf));
    }

    @Test
    public void type_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("string", "foo");
        check.put("string_null", null);
        check.put("int", 1);
        check.put("long", 1L);
        check.put("double", 1.12D);

        assertFalse(filter(where("string_null").type(String.class)).apply(check, conf));
        assertTrue(filter(where("string").type(String.class)).apply(check, conf));
        assertFalse(filter(where("string").type(Number.class)).apply(check, conf));

        assertTrue(filter(where("int").type(Integer.class)).apply(check, conf));
        assertFalse(filter(where("int").type(Long.class)).apply(check, conf));

        assertTrue(filter(where("long").type(Long.class)).apply(check, conf));
        assertFalse(filter(where("long").type(Integer.class)).apply(check, conf));

        assertTrue(filter(where("double").type(Double.class)).apply(check, conf));
        assertFalse(filter(where("double").type(Integer.class)).apply(check, conf));
    }

    @Test
    public void pattern_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("name", "kalle");
        check.put("name_null", null);

        assertFalse(filter(where("name_null").regex(Pattern.compile(".alle"))).apply(check, conf));
        assertTrue(filter(where("name").regex(Pattern.compile(".alle"))).apply(check, conf));
        assertFalse(filter(where("name").regex(Pattern.compile("KALLE"))).apply(check, conf));
        assertTrue(
                filter(where("name").regex(Pattern.compile("KALLE", Pattern.CASE_INSENSITIVE)))
                        .apply(check, conf));
    }

    @Test
    public void combine_filter_deep_criteria() {

        String json =
"""
[
   {
      "first-name" : "John",
      "last-name" : "Irving",
      "address" : {"state" : "Texas"}
   },
   {
      "first-name" : "Jock",
      "last-name" : "Ewing",
      "address" : {"state" : "Texas"}
   },
   {
      "first-name" : "Jock",
      "last-name" : "Barnes",
      "address" : {"state" : "Nevada"}
   }\s
]\
""";

        Filter filter = filter(where("first-name").is("Jock").and("address.state").is("Texas"));

        List<Map<String, Object>> jocksInTexas1 = JsonPath.read(json, "$[?]", filter);
        List<Map<String, Object>> jocksInTexas2 =
                JsonPath.read(json, "$[?(@.first-name == 'Jock' && @.address.state == 'Texas')]");

        JsonPath.parse(json).json();

        assertThat((String) JsonPath.read(jocksInTexas1, "[0].address.state"), is("Texas"));
        assertThat((String) JsonPath.read(jocksInTexas1, "[0].first-name"), is("Jock"));
        assertThat((String) JsonPath.read(jocksInTexas1, "[0].last-name"), is("Ewing"));

        System.out.println("res1" + jocksInTexas1);
        System.out.println("res2" + jocksInTexas2);

        System.out.println("done");
    }

    @Test
    public void filters_can_be_combined() throws Exception {

        Map<String, Object> check = new HashMap<String, Object>();
        check.put("string", "foo");
        check.put("string_null", null);
        check.put("int", 10);
        check.put("long", 1L);
        check.put("double", 1.12D);

        Filter shouldMarch = filter(where("string").is("foo").and("int").lt(11));
        Filter shouldNotMarch = filter(where("string").is("foo").and("int").gt(11));

        assertTrue(shouldMarch.apply(check, conf));
        assertFalse(shouldNotMarch.apply(check, conf));
    }

    @Test
    public void arrays_of_maps_can_be_filtered() throws Exception {}

    @Test
    public void arrays_of_objects_can_be_filtered() throws Exception {}

    @Test
    public void filters_can_contain_json_path_expressions() throws Exception {
        System.out.println(DOCUMENT);
        Object doc = JsonProviderFactory.createProvider().parse(DOCUMENT);

        assertFalse(filter(where("$.store.bicycle.color").ne("red")).apply(doc, conf));
    }

    @Test
    public void not_empty_filter_evaluates() {

        String json =
"""
{
    "fields": [
        {
            "errors": [],\s
            "name": "",\s
            "empty": true\s
        },\s
        {
            "errors": [],\s
            "name": "foo"
        },\s
        {
            "errors": [
                "first",\s
                "second"
            ],\s
            "name": "invalid"
        }
    ]
}
""";

        Object doc = JsonProviderFactory.createProvider().parse(json);

        List<Map<String, Object>> result =
                JsonPath.read(doc, "$.fields[?]", filter(where("errors").notEmpty()));
        assertEquals(1, result.size());
        System.out.println(result);

        List<Map<String, Object>> result2 =
                JsonPath.read(doc, "$.fields[?]", filter(where("name").notEmpty()));
        assertEquals(2, result2.size());
        System.out.println(result2);
    }

    @Test
    public void filter_path_must_be_absolute() {
        assertThrows(
                InvalidCriteriaException.class,
                () -> {
                    where("$.store.*").size(4);
                });
    }
}
