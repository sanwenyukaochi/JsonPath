package com.jayway.jsonpath;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class JsonPathTest {

    public static final String DOCUMENT =
            """
            { "store": {
                "book": [
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
                  "foo:bar": "fooBar"
                }
              }
            }\
            """;

    @Test
    public void read_path_with_colon() throws Exception {
        assertEquals("fooBar", JsonPath.read(DOCUMENT, "$.store.bicycle.foo:bar"));
        assertEquals("fooBar", JsonPath.read(DOCUMENT, "$.['store'].['bicycle'].['foo:bar']"));
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
    }

    @Test
    public void all_authors() throws Exception {
        assertThat(
                JsonPath.<List<String>>read(DOCUMENT, "$..author"),
                hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
    }

    @Test
    public void all_store_properties() throws Exception {
        List<Object> itemsInStore = JsonPath.read(DOCUMENT, "$.store.*");

        int bookIndex = JsonUtil.isList(itemsInStore.get(0)) ? 0 : 1;
        assertEquals("Nigel Rees", JsonPath.read(itemsInStore, "$.[" + bookIndex + "].[0].author"));
        assertEquals("Nigel Rees", JsonPath.read(itemsInStore, "$.[" + bookIndex + "][0].author"));
    }

    @Test
    public void all_prices_in_store() throws Exception {

        assertThat(
                JsonPath.<List<Double>>read(DOCUMENT, "$.store..price"),
                hasItems(8.95D, 12.99D, 8.99D, 19.95D));
    }

    @Test
    public void access_array_by_index_from_tail() throws Exception {

        assertThat(
                JsonPath.<String>read(DOCUMENT, "$..book[(@.length-1)].author"),
                equalTo("J. R. R. Tolkien"));
        assertThat(
                JsonPath.<String>read(DOCUMENT, "$..book[-1:].author"),
                equalTo("J. R. R. Tolkien"));
    }

    @Test
    public void read_store_book_index_0_and_1() throws Exception {

        assertThat(
                JsonPath.<List<String>>read(DOCUMENT, "$.store.book[0,1].author"),
                hasItems("Nigel Rees", "Evelyn Waugh"));
        assertTrue(JsonPath.<List<String>>read(DOCUMENT, "$.store.book[0,1].author").size() == 2);
    }

    @Test
    public void read_store_book_pull_first_2() throws Exception {

        assertThat(
                JsonPath.<List<String>>read(DOCUMENT, "$.store.book[:2].author"),
                hasItems("Nigel Rees", "Evelyn Waugh"));
        assertTrue(JsonPath.<List<String>>read(DOCUMENT, "$.store.book[:2].author").size() == 2);
    }

    @Test
    public void read_store_book_filter_by_isbn() throws Exception {

        assertThat(
                JsonPath.<List<String>>read(DOCUMENT, "$.store.book[?(@.isbn)].isbn"),
                hasItems("0-553-21311-3", "0-395-19395-8"));
        assertTrue(
                JsonPath.<List<String>>read(DOCUMENT, "$.store.book[?(@.isbn)].isbn").size() == 2);
    }

    @Test
    public void all_books_cheaper_than_10() throws Exception {

        assertThat(
                JsonPath.<List<String>>read(DOCUMENT, "$.store.book[?(@.price < 10)].title"),
                hasItems("Sayings of the Century", "Moby Dick"));
    }

    @Test
    public void double_comparison_does_not_truncate_book_prices() throws Exception {
        assertEquals(
                List.of(
                        "Sayings of the Century",
                        "Sword of Honour",
                        "Moby Dick",
                        "The Lord of the Rings"),
                JsonPath.read(DOCUMENT, "$.store.book[?(@.price > 8)].title"));
    }

    @Test
    public void reads_books_with_a_different_category() throws Exception {
        List<String> expected = List.of("Sword of Honour", "Moby Dick", "The Lord of the Rings");
        assertEquals(
                expected,
                JsonPath.read(DOCUMENT, "$.store.book[?(@.category != 'reference')].title"));
        assertEquals(
                expected,
                JsonPath.read(DOCUMENT, "$.store.book[?(@.category <> 'reference')].title"));
    }

    @Test
    public void all_books_with_category_reference() throws Exception {
        assertThat(
                JsonPath.<List<String>>read(DOCUMENT, "$..book[?(@.category = 'reference')].title"),
                hasItems("Sayings of the Century"));
    }

    @Test
    public void all_members_of_all_documents() throws Exception {

        List<String> all = JsonPath.read(DOCUMENT, "$..*");
    }

    @Test
    public void access_index_out_of_bounds_does_not_throw_exception() throws Exception {
        Object res = JsonPath.read(DOCUMENT, "$.store.book[100].author");
        assertNull(res);

        res = JsonPath.read(DOCUMENT, "$.store.book[1, 200].author");
        assertThat((List<String>) res, hasItems("Evelyn Waugh"));
    }

    @Test
    public void read_returns_null_when_path_has_no_matches() throws Exception {
        assertNull(JsonPath.read(DOCUMENT, "$.store.book[1000].title"));
        assertNull(JsonPath.read(DOCUMENT, "$.store.book[0].fooBar"));
    }

    @Test
    public void read_returns_a_list_when_path_matches_multiple_values() throws Exception {
        assertEquals(
                List.of(
                        "Sayings of the Century",
                        "Sword of Honour",
                        "Moby Dick",
                        "The Lord of the Rings"),
                JsonPath.read(DOCUMENT, "$.store.book[*].title"));
    }

    @Test
    public void paths_with_spaces_are_treated_as_property_names() throws Exception {
        assertNull(JsonPath.read(DOCUMENT, "space is not good"));
        assertEquals(
                "value", JsonPath.read(Map.of("space is not good", "value"), "space is not good"));
    }

    @Test
    public void invalid_new_path_throws_exception() {
        assertThrows(InvalidPathException.class, () -> JsonPath.read(DOCUMENT, "new "));
    }
}
