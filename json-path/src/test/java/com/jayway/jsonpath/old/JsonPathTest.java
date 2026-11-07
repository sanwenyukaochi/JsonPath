package com.jayway.jsonpath.old;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.jayway.jsonpath.BaseTest;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.internal.path.PathCompiler;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class JsonPathTest extends BaseTest {

    static {
    }

    public static final String ARRAY =
            "[{\"value\": 1},{\"value\": 2}, {\"value\": 3},{\"value\": 4}]";

    public static final String DOCUMENT =
"""
{ "store": {
    "book": [\s
      { "category": "reference",
        "author": "Nigel Rees",
        "title": "Sayings of the Century",
        "display-price": 8.95
      },
      { "category": "fiction",
        "author": "Evelyn Waugh",
        "title": "Sword of Honour",
        "display-price": 12.99
      },
      { "category": "fiction",
        "author": "Herman Melville",
        "title": "Moby Dick",
        "isbn": "0-553-21311-3",
        "display-price": 8.99
      },
      { "category": "fiction",
        "author": "J. R. R. Tolkien",
        "title": "The Lord of the Rings",
        "isbn": "0-395-19395-8",
        "display-price": 22.99
      }
    ],
    "bicycle": {
      "color": "red",
      "display-price": 19.95,
      "foo:bar": "fooBar",
      "dot.notation": "new",
      "dash-notation": "dashes"
    }
  }
}\
""";

    public static final Object OBJ_DOCUMENT = JsonPath.parse(DOCUMENT).json();

    private static final String PRODUCT_JSON =
"""
{
\t"product": [ {
\t    "version": "A",\s
\t    "codename": "Seattle",\s
\t    "attr.with.dot": "A"
\t},
\t{
\t    "version": "4.0",\s
\t    "codename": "Montreal",\s
\t    "attr.with.dot": "B"
\t}]
}\
""";

    private static final String ARRAY_EXPAND =
            "[{\"parent\": \"ONE\", \"child\": {\"name\": \"NAME_ONE\"}}, [{\"parent\": \"TWO\", \"child\": {\"name\": \"NAME_TWO\"}}]]";

    @Test
    public void missing_prop() {
        assertThrows(
                PathNotFoundException.class,
                () -> {
                    Object read2 =
                            JsonPath.using(
                                            Configuration.defaultConfiguration()
                                                    .addOptions(Option.REQUIRE_PROPERTIES))
                                    .parse(DOCUMENT)
                                    .read("$.store.book[*].fooBar.not");
                });
    }

    @Test
    public void bracket_notation_with_dots() {
        String json =
"""
{
    "store": {
        "book": [
            {
                "author.name": "Nigel Rees",\s
                "category": "reference",\s
                "price": 8.95,\s
                "title": "Sayings of the Century"
            }
        ]
    }
}\
""";

        assertEquals("Nigel Rees", JsonPath.read(json, "$.store.book[0]['author.name']"));
    }

    @Test
    public void null_object_in_path() {

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
  "data2": {
    "user": 3,
    "own": null,
    "passes": [{"id":"1"}],
    "completed": null
  },
  "version": 1371160528774
}\
""";
        try {
            JsonPath.read(json, "$.data.passes[0].id");
            Assertions.fail("Expected PathNotFoundException");
        } catch (PathNotFoundException e) {
        }
        Assertions.assertThat(JsonPath.<String>read(json, "$.data2.passes[0].id")).isEqualTo("1");
    }

    @Test
    public void array_start_expands() throws Exception {

        assertThat(
                JsonPath.<List<String>>read(ARRAY_EXPAND, "$[?(@['parent'] == 'ONE')].child.name"),
                hasItems("NAME_ONE"));
    }

    @Test
    public void bracket_notation_can_be_used_in_path() throws Exception {

        assertEquals("new", JsonPath.read(DOCUMENT, "$.['store'].bicycle.['dot.notation']"));
        assertEquals("new", JsonPath.read(DOCUMENT, "$['store']['bicycle']['dot.notation']"));
        assertEquals("new", JsonPath.read(DOCUMENT, "$.['store']['bicycle']['dot.notation']"));
        assertEquals("new", JsonPath.read(DOCUMENT, "$.['store'].['bicycle'].['dot.notation']"));

        assertEquals("dashes", JsonPath.read(DOCUMENT, "$.['store'].bicycle.['dash-notation']"));
        assertEquals("dashes", JsonPath.read(DOCUMENT, "$['store']['bicycle']['dash-notation']"));
        assertEquals("dashes", JsonPath.read(DOCUMENT, "$.['store']['bicycle']['dash-notation']"));
        assertEquals(
                "dashes", JsonPath.read(DOCUMENT, "$.['store'].['bicycle'].['dash-notation']"));
    }

    @Test
    public void filter_an_array() throws Exception {
        List<Object> matches = JsonPath.read(ARRAY, "$.[?(@.value == 1)]");

        assertEquals(1, matches.size());
    }

    @Test
    public void filter_an_array_on_index() throws Exception {
        Integer matches = JsonPath.read(ARRAY, "$.[1].value");

        assertEquals(new Integer(2), matches);
    }

    @Test
    public void read_path_with_colon() throws Exception {

        assertEquals(JsonPath.read(DOCUMENT, "$['store']['bicycle']['foo:bar']"), "fooBar");
    }

    @Test
    public void read_document_from_root() throws Exception {

        Map result = JsonPath.read(DOCUMENT, "$.store");

        assertEquals(2, result.values().size());
    }

    @Test
    public void read_store_book_1() throws Exception {

        JsonPath path = JsonPath.compile("$.store.book[1]");

        Map map = path.read(DOCUMENT);

        assertEquals("Evelyn Waugh", map.get("author"));
    }

    @Test
    public void read_store_book_wildcard() throws Exception {
        JsonPath path = JsonPath.compile("$.store.book[*]");

        List<Object> list = path.read(DOCUMENT);
        Assertions.assertThat(list.size()).isEqualTo(4);
    }

    @Test
    public void read_store_book_author() throws Exception {
        assertThat(
                JsonPath.<List<String>>read(DOCUMENT, "$.store.book[0,1].author"),
                hasItems("Nigel Rees", "Evelyn Waugh"));
        assertThat(
                JsonPath.<List<String>>read(DOCUMENT, "$.store.book[*].author"),
                hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
        assertThat(
                JsonPath.<List<String>>read(DOCUMENT, "$.['store'].['book'][*].['author']"),
                hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
        assertThat(
                JsonPath.<List<String>>read(DOCUMENT, "$['store']['book'][*]['author']"),
                hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
        assertThat(
                JsonPath.<List<String>>read(DOCUMENT, "$['store'].book[*]['author']"),
                hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
    }

    @Test
    public void all_authors() throws Exception {
        assertThat(
                JsonPath.<List<String>>read(DOCUMENT, "$..author"),
                hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
    }

    @Test
    public void all_store_properties() throws Exception {

        List<String> result =
                PathCompiler.compile("$.store.*")
                        .evaluate(OBJ_DOCUMENT, OBJ_DOCUMENT, Configuration.defaultConfiguration())
                        .getPathList();

        Assertions.assertThat(result).containsOnly("$['store']['bicycle']", "$['store']['book']");
    }

    @Test
    public void all_prices_in_store() throws Exception {

        assertThat(
                JsonPath.<List<Double>>read(DOCUMENT, "$.store..['display-price']"),
                hasItems(8.95D, 12.99D, 8.99D, 19.95D));
    }

    @Test
    public void access_array_by_index_from_tail() throws Exception {

        assertThat(
                JsonPath.<List<String>>read(DOCUMENT, "$..book[1:].author"),
                hasItems("Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
    }

    @Test
    public void read_store_book_index_0_and_1() throws Exception {

        assertThat(
                JsonPath.<List<String>>read(DOCUMENT, "$.store.book[0,1].author"),
                hasItems("Nigel Rees", "Evelyn Waugh"));
        assertTrue(JsonPath.<List>read(DOCUMENT, "$.store.book[0,1].author").size() == 2);
    }

    @Test
    public void read_store_book_pull_first_2() throws Exception {

        assertThat(
                JsonPath.<List<String>>read(DOCUMENT, "$.store.book[:2].author"),
                hasItems("Nigel Rees", "Evelyn Waugh"));
        assertTrue(JsonPath.<List>read(DOCUMENT, "$.store.book[:2].author").size() == 2);
    }

    @Test
    public void read_store_book_filter_by_isbn() throws Exception {

        assertThat(
                JsonPath.<List<String>>read(DOCUMENT, "$.store.book[?(@.isbn)].isbn"),
                hasItems("0-553-21311-3", "0-395-19395-8"));
        assertTrue(JsonPath.<List>read(DOCUMENT, "$.store.book[?(@.isbn)].isbn").size() == 2);
        assertTrue(JsonPath.<List>read(DOCUMENT, "$.store.book[?(@['isbn'])].isbn").size() == 2);
    }

    @Test
    public void all_books_cheaper_than_10() throws Exception {

        assertThat(
                JsonPath.<List<String>>read(DOCUMENT, "$..book[?(@['display-price'] < 10)].title"),
                hasItems("Sayings of the Century", "Moby Dick"));
        assertThat(
                JsonPath.<List<String>>read(DOCUMENT, "$..book[?(@.display-price < 10)].title"),
                hasItems("Sayings of the Century", "Moby Dick"));
    }

    @Test
    public void all_books() throws Exception {
        Assertions.assertThat(JsonPath.<List<Object>>read(DOCUMENT, "$..book")).hasSize(1);
    }

    @Test
    public void dot_in_predicate_works() throws Exception {

        assertThat(
                JsonPath.<List<String>>read(
                        PRODUCT_JSON, "$.product[?(@.version=='4.0')].codename"),
                hasItems("Montreal"));
    }

    @Test
    public void dots_in_predicate_works() throws Exception {

        assertThat(
                JsonPath.<List<String>>read(
                        PRODUCT_JSON, "$.product[?(@.['attr.with.dot']=='A')].codename"),
                hasItems("Seattle"));
    }

    @Test
    public void all_books_with_category_reference() throws Exception {

        assertThat(
                JsonPath.<List<String>>read(DOCUMENT, "$..book[?(@.category=='reference')].title"),
                hasItems("Sayings of the Century"));
        assertThat(
                JsonPath.<List<String>>read(
                        DOCUMENT, "$.store.book[?(@.category=='reference')].title"),
                hasItems("Sayings of the Century"));
    }

    @Test
    public void all_members_of_all_documents() throws Exception {
        List<String> all = JsonPath.read(DOCUMENT, "$..*");
    }

    @Test
    public void access_index_out_of_bounds_does_not_throw_exception() throws Exception {
        assertThrows(
                PathNotFoundException.class,
                () -> {
                    JsonPath.read(DOCUMENT, "$.store.book[100].author");
                });
    }

    @Test
    public void exists_filter_with_nested_path() throws Exception {

        assertThat(JsonPath.<List<String>>read(DOCUMENT, "$..[?(@.bicycle.color)]"), hasSize(1));
        assertThat(
                JsonPath.<List<String>>read(DOCUMENT, "$..[?(@.bicycle.numberOfGears)]"),
                hasSize(0));
    }

    @Test
    public void invalid_paths_throw_invalid_path_exception() throws Exception {
        for (String path : new String[] {"$.", "$.results[?"}) {
            try {
                JsonPath.compile(path);
            } catch (InvalidPathException e) {

            } catch (Exception e) {
                fail(
                        "Expected an InvalidPathException trying to compile '"
                                + path
                                + "', but got a "
                                + e.getClass().getName());
            }
        }
    }
}
