package com.jayway.jsonpath.old;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jayway.jsonpath.BaseTest;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Criteria;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.Predicate.PredicateContext;
import com.jayway.jsonpath.internal.Cache;
import com.jayway.jsonpath.internal.CompiledPath;
import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.Utils;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minidev.json.JSONAware;
import net.minidev.json.parser.JSONParser;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

public class IssuesTest extends BaseTest {

    private static final JsonProvider jp = Configuration.defaultConfiguration().jsonProvider();

    @Test
    public void full_ones_can_be_filtered() {
        String json =
                """
                [
                 {"kind" : "full"},
                 {"kind" : "empty"}
                ]
                """;

        List<Map<String, String>> fullOnes = JsonPath.read(json, "$[?(@.kind == full)]");

        assertEquals(1, fullOnes.size());
        assertEquals("full", fullOnes.get(0).get("kind"));
    }

    @Test
    public void issue_36() {
        String json =
"""
{

 "arrayOfObjectsAndArrays" : [ { "k" : ["json"] }, { "k":["path"] }, { "k" : ["is"] }, { "k" : ["cool"] } ],

  "arrayOfObjects" : [{"k" : "json"}, {"k":"path"}, {"k" : "is"}, {"k" : "cool"}]

 }\
""";

        Object o1 = JsonPath.read(json, "$.arrayOfObjectsAndArrays..k ");
        Object o2 = JsonPath.read(json, "$.arrayOfObjects..k ");

        assertEquals("[[\"json\"],[\"path\"],[\"is\"],[\"cool\"]]", jp.toJson(o1));
        assertEquals("[\"json\",\"path\",\"is\",\"cool\"]", jp.toJson(o2));
    }

    @Test
    public void issue_11() throws Exception {
        String json = "{ \"foo\" : [] }";
        List<String> result = JsonPath.read(json, "$.foo[?(@.rel == 'item')][0].uri");
        assertTrue(result.isEmpty());
    }

    @Test
    public void issue_11b() throws Exception {
        assertThrows(
                PathNotFoundException.class,
                () -> {
                    String json = "{ \"foo\" : [] }";
                    JsonPath.read(json, "$.foo[0].uri");
                });
    }

    @Test
    public void issue_15() throws Exception {
        String json =
"""
{ "store": {
    "book": [\s
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
        "retailer": null,\s
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

            is.close();
        } catch (Exception e) {

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
    public void issue_37() {
        String json =
"""
[
    {
        "id": "9",
        "sku": "SKU-001",
        "compatible": false
    },
    {
        "id": "13",
        "sku": "SKU-005",
        "compatible": true
    },
    {
        "id": "11",
        "sku": "SKU-003",
        "compatible": true
    }
]\
""";

        List<String> result = JsonPath.read(json, "$[?(@.compatible == true)].sku");

        Assertions.assertThat(result).containsExactly("SKU-005", "SKU-003");
    }

    @Test
    public void issue_38() {
        String json =
"""
{
   "datapoints":[
      [
         10.1,
         13.0
      ],
      [
         21.0,
         22.0
      ]
   ]
}\
""";

        List<Double> result = JsonPath.read(json, "$.datapoints.[*].[0]");

        assertThat(result.get(0), is(new Double(10.1)));
        assertThat(result.get(1), is(new Double(21.0)));
    }

    @Test
    public void issue_39() {
        String json =
"""
{
    "obj1": {
        "arr": ["1", "2"]
    },
    "obj2": {
       "arr": ["3", "4"]
    }
}
""";

        List<String> result = JsonPath.read(json, "$..arr");
        assertThat(result.size(), is(2));
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
                    Configuration configuration = Configuration.defaultConfiguration();

                    String json = "{\"a\":{\"b\":1,\"c\":2}}";
                    System.out.println((Object) JsonPath.parse(json, configuration).read("a.d"));
                });
    }

    @Test
    public void issue_22c() throws Exception {
        Configuration configuration =
                Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS).build();

        String json = "{\"a\":{\"b\":1,\"c\":2}}";
        assertNull(JsonPath.parse(json, configuration).read("a.d"));
    }

    @Test
    public void issue_22b() throws Exception {
        String json = "{\"a\":[{\"b\":1,\"c\":2},{\"b\":5,\"c\":2}]}";
        List<Object> res =
                JsonPath.using(
                                Configuration.defaultConfiguration()
                                        .setOptions(Option.DEFAULT_PATH_LEAF_TO_NULL))
                        .parse(json)
                        .read("a[?(@.b==5)].d");
        Assertions.assertThat(res).hasSize(1).containsNull();
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
                "{\"list\": [ { \"a\":\"atext\", \"b.b-a\":\"batext2\", \"b\":{ \"b-a\":\"batext\", \"b-b\":\"bbtext\" } }, { \"a\":\"atext2\", \"b\":{ \"b-a\":\"batext2\", \"b-b\":\"bbtext2\" } } ] }";

        List<Map<String, Object>> result = JsonPath.read(json, "$.list[?(@['b.b-a']=='batext2')]");
        assertEquals(1, result.size());
        Object a = result.get(0).get("a");
        assertEquals("atext", a);

        result = JsonPath.read(json, "$.list[?(@.b.b-a=='batext2')]");
        assertEquals(1, result.size());
        assertEquals("atext2", result.get(0).get("a"));
    }

    @Test
    public void issue_29_b() throws Exception {
        String json =
                "{\"list\": [ { \"a\":\"atext\", \"b\":{ \"b-a\":\"batext\", \"b-b\":\"bbtext\" } }, { \"a\":\"atext2\", \"b\":{ \"b-a\":\"batext2\", \"b-b\":\"bbtext2\" } } ] }";
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
    "book": [\s
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

        Assertions.assertThat(result).hasSize(1);
        Assertions.assertThat(result.get(0)).containsEntry("title", "Sayings of the Century");
    }

    @Test
    public void array_root() {
        String json =
"""
[
    {
        "a": 1,
        "b": 2,
        "c": 3
    }
]\
""";

        assertEquals(1, JsonPath.<Integer>read(json, "$[0].a"));
    }

    @Test
    public void a_test() {
        assertThrows(
                PathNotFoundException.class,
                () -> {
                    String json =
                            """
                            {
                              "success": true,
                              "data": {
                                "user": 3,
                                "own": null,
                                "passes": null,
                                "completed": null
                              },
                              "version": 1371160528774
                            }\
                            """;

                    Object read = JsonPath.read(json, "$.data.passes[0].id");
                });
    }

    @Test
    public void issue_42() {
        String json =
                """
                {
                    "list": [{
                        "name": "My (String)"
                    }]
                }
                """;

        List<Map<String, String>> result =
                JsonPath.read(json, "$.list[?(@.name == 'My (String)')]");

        Assertions.assertThat(result)
                .containsExactly(Collections.singletonMap("name", "My (String)"));
    }

    @Test
    public void issue_43() {
        String json = "{\"test\":null}";

        Assertions.assertThat(JsonPath.<Object>read(json, "test")).isNull();

        Assertions.assertThat(
                        JsonPath.using(
                                        Configuration.defaultConfiguration()
                                                .setOptions(Option.SUPPRESS_EXCEPTIONS))
                                .parse(json)
                                .<Object>read("nonExistingProperty"))
                .isNull();

        try {
            JsonPath.read(json, "nonExistingProperty");

            failBecauseExceptionWasNotThrown(PathNotFoundException.class);
        } catch (PathNotFoundException e) {

        }

        try {
            JsonPath.read(json, "nonExisting.property");

            failBecauseExceptionWasNotThrown(PathNotFoundException.class);
        } catch (PathNotFoundException e) {

        }
    }

    @Test
    public void issue_45() {
        String json = "{\"rootkey\":{\"sub.key\":\"value\"}}";

        Assertions.assertThat(JsonPath.<Object>read(json, "rootkey['sub.key']")).isEqualTo("value");
    }

    @Test
    public void issue_46() {
        String json = "{\"a\": {}}";

        Configuration configuration =
                Configuration.defaultConfiguration().setOptions(Option.SUPPRESS_EXCEPTIONS);
        Assertions.assertThat(JsonPath.using(configuration).parse(json).<Object>read("a.x"))
                .isNull();

        try {
            JsonPath.read(json, "a.x");

            failBecauseExceptionWasNotThrown(PathNotFoundException.class);
        } catch (PathNotFoundException e) {
            Assertions.assertThat(e).hasMessage("No results for path: $['a']['x']");
        }
    }

    @Test
    public void issue_x() {

        String json =
"""
{
 "a" : [
   {},
   { "b" : [ { "c" : "foo"} ] }
 ]
}
""";

        List<String> result = JsonPath.read(json, "$.a.*.b.*.c");

        Assertions.assertThat(result).containsExactly("foo");
    }

    @Test
    public void issue_60() {

        String json =
"""
[
{
  "mpTransactionId": "542986eae4b001fd500fdc5b-coreDisc_50-title",
  "resultType": "FAIL",
  "narratives": [
    {
      "ruleProcessingDate": "Nov 2, 2014 7:30:20 AM",
      "area": "Discovery",
      "phase": "Validation",
      "message": "Chain does not have a discovery event. Possible it was cut by the date that was picked",
      "ruleName": "Validate chain\\u0027s discovery event existence",
      "lastRule": true
    }
  ]
},
{
  "mpTransactionId": "54298649e4b001fd500fda3e-fixCoreDiscovery_3-title",
  "resultType": "FAIL",
  "narratives": [
    {
      "ruleProcessingDate": "Nov 2, 2014 7:30:20 AM",
      "area": "Discovery",
      "phase": "Validation",
      "message": "There is one and only discovery event ContentDiscoveredEvent(230) found.",
      "ruleName": "Marks existence of discovery event (230)",
      "lastRule": false
    },
    {
      "ruleProcessingDate": "Nov 2, 2014 7:30:20 AM",
      "area": "Discovery/Processing",
      "phase": "Validation",
      "message": "Chain does not have SLA start event (204) in Discovery or Processing. ",
      "ruleName": "Check if SLA start event is not present (204). ",
      "lastRule": false
    },
    {
      "ruleProcessingDate": "Nov 2, 2014 7:30:20 AM",
      "area": "Processing",
      "phase": "Transcode",
      "message": "No start transcoding events found",
      "ruleName": "Start transcoding events missing (240)",
      "lastRule": true
    }
  ]
}]\
""";

        List<String> problems = JsonPath.read(json, "$..narratives[?(@.lastRule==true)].message");

        Assertions.assertThat(problems)
                .containsExactly(
                        "Chain does not have a discovery event. Possible it was cut by the date that was picked",
                        "No start transcoding events found");
    }

    @Test
    public void issue_71() {
        String json =
"""
{
    "logs": [
        {
            "message": "it's here",
            "id": 2
        }
    ]
}\
""";

        List<String> result = JsonPath.read(json, "$.logs[?(@.message == 'it\\'s here')].message");

        Assertions.assertThat(result).containsExactly("it's here");
    }

    @Test
    public void stack_overflow_question_1() {

        String json =
"""
{
"store": {
    "book": [
        {
            "category": "reference",
            "authors" : [
                 {
                     "firstName" : "Nigel",
                     "lastName" :  "Rees"
                  }
            ],
            "title": "Sayings of the Century",
            "price": 8.95
        },
        {
            "category": "fiction",
            "authors": [
                 {
                     "firstName" : "Evelyn",
                     "lastName" :  "Waugh"
                  },
                 {
                     "firstName" : "Another",
                     "lastName" :  "Author"
                  }
            ],
            "title": "Sword of Honour",
            "price": 12.99
        }
    ]
  }
}\
""";

        Filter filter = Filter.filter(Criteria.where("authors[*].lastName").contains("Waugh"));

        Object read = JsonPath.parse(json).read("$.store.book[?]", filter);

        System.out.println(read);
    }

    @Test
    public void issue_76() throws Exception {

        String json =
"""
{
    "cpus": -8.88178419700125e-16,
    "disk": 0,
    "mem": 0
}\
""";

        JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
        JSONAware jsonModel = (JSONAware) parser.parse(json);

        jsonModel.toJSONString();
    }

    @Test
    public void issue_97() throws Exception {
        String json =
"""
{
    "books": [
        {
            "category": "fiction"
        },
        {
            "category": "reference"
        },
        {
            "category": "fiction"
        },
        {
            "category": "fiction"
        },
        {
            "category": "reference"
        },
        {
            "category": "fiction"
        },
        {
            "category": "reference"
        },
        {
            "category": "reference"
        },
        {
            "category": "reference"
        },
        {
            "category": "reference"
        },
        {
            "category": "reference"
        }
    ]
}
""";

        Configuration conf =
                Configuration.builder()
                        .jsonProvider(new GsonJsonProvider())
                        .mappingProvider(new GsonMappingProvider())
                        .build();

        DocumentContext context = JsonPath.using(conf).parse(json);
        context.delete("$.books[?(@.category == 'reference')]");

        List<String> categories = context.read("$..category", List.class);
        Assertions.assertThat(categories).containsOnly("fiction");
    }

    @Test
    public void issue_79() throws Exception {
        String json =
"""
{\s
  "c": {
    "d1": {
      "url": [ "url1", "url2" ]
    },
    "d2": {
      "url": [ "url3", "url4","url5" ]
    }
  }
}\
""";

        List<String> res = JsonPath.read(json, "$.c.*.url[2]");

        Assertions.assertThat(res).containsExactly("url5");
    }

    @Test
    public void issue_99() throws Exception {
        String json =
"""
{
    "array1": [
        {
            "array2": []
        },
        {
            "array2": [
                {
                    "key": "test_key"
                }
            ]
        }
    ]
}\
""";

        Configuration configuration =
                Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);

        List<String> keys =
                JsonPath.using(configuration).parse(json).read("$.array1[*].array2[0].key");

        System.out.println(keys);
    }

    @Test
    public void issue_94_1() throws Exception {
        Cache cache = new Cache(200);
        Path dummy = new CompiledPath(null, false);
        for (int i = 0; i < 10000; ++i) {
            String key = String.valueOf(i);
            cache.get(key);
            cache.put(key, dummy);
        }
        Thread.sleep(2000);

        Assertions.assertThat(cache.size()).isEqualTo(200);
    }

    @Test
    public void issue_94_2() throws Exception {
        Cache cache = new Cache(5);

        Path dummy = new CompiledPath(null, false);

        cache.put("1", dummy);
        cache.put("2", dummy);
        cache.put("3", dummy);
        cache.put("4", dummy);
        cache.put("5", dummy);
        cache.put("6", dummy);

        cache.get("1");
        cache.get("2");
        cache.get("3");
        cache.get("4");
        cache.get("5");
        cache.get("6");

        cache.get("2");
        cache.get("3");
        cache.get("4");
        cache.get("5");
        cache.get("6");

        cache.get("3");
        cache.get("4");
        cache.get("5");
        cache.get("6");

        cache.get("4");
        cache.get("5");
        cache.get("6");

        cache.get("5");
        cache.get("6");

        cache.get("6");

        Assertions.assertThat(cache.getSilent("6")).isNotNull();
        Assertions.assertThat(cache.getSilent("5")).isNotNull();
        Assertions.assertThat(cache.getSilent("4")).isNotNull();
        Assertions.assertThat(cache.getSilent("3")).isNotNull();
        Assertions.assertThat(cache.getSilent("2")).isNotNull();
        Assertions.assertThat(cache.getSilent("1")).isNull();
    }

    @Test
    public void issue_129() throws Exception {

        final Map<String, Integer> match = new HashMap<String, Integer>();
        match.put("a", 1);
        match.put("b", 2);

        Map<String, Integer> noMatch = new HashMap<String, Integer>();
        noMatch.put("a", -1);
        noMatch.put("b", -2);

        Filter orig = filter(where("a").eq(1).and("b").eq(2));

        String filterAsString = orig.toString();

        Filter parsed = Filter.parse(filterAsString);

        Assertions.assertThat(orig.apply(createPredicateContext(match))).isTrue();
        Assertions.assertThat(parsed.apply(createPredicateContext(match))).isTrue();
        Assertions.assertThat(orig.apply(createPredicateContext(noMatch))).isFalse();
        Assertions.assertThat(parsed.apply(createPredicateContext(noMatch))).isFalse();
    }

    private PredicateContext createPredicateContext(final Map<String, Integer> map) {
        return new PredicateContext() {
            @Override
            public Object item() {
                return map;
            }

            @Override
            public <T> T item(Class<T> clazz) throws MappingException {
                return (T) map;
            }

            @Override
            public Object root() {
                return map;
            }

            @Override
            public Configuration configuration() {
                return Configuration.defaultConfiguration();
            }
        };
    }

    @Test
    public void issue_131() {

        String json =
"""
[
    {
        "foo": "1"
    },
    {
        "foo": null
    },
    {
        "xxx": null
    }
]\
""";

        List<Map<String, String>> result = JsonPath.read(json, "$[?(@.foo)]");

        Assertions.assertThat(result).extracting("foo").containsExactly("1", null);
    }

    @Test
    public void issue_131_2() {

        String json =
"""
[
    {
        "foo": { "bar" : "0"}
    },
    {
        "foo": null
    },
    {
        "xxx": null
    }
]\
""";

        List<String> result = JsonPath.read(json, "$[?(@.foo != null)].foo.bar");

        Assertions.assertThat(result).containsExactly("0");

        result = JsonPath.read(json, "$[?(@.foo.bar)].foo.bar");

        Assertions.assertThat(result).containsExactly("0");
    }
}
