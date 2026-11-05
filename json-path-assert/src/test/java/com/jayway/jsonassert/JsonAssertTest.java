package com.jayway.jsonassert;

import static com.jayway.jsonassert.JsonAssert.collectionWithSize;
import static com.jayway.jsonassert.JsonAssert.emptyCollection;
import static com.jayway.jsonassert.JsonAssert.mapContainingKey;
import static com.jayway.jsonassert.JsonAssert.mapContainingValue;
import static com.jayway.jsonassert.JsonAssert.with;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import org.junit.jupiter.api.Test;

public class JsonAssertTest {

    public static final String JSON =
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
                  "nullValue": null
                }
              }
            }\
            """;

    @Test
    public void links_document() throws Exception {
        with(getResourceAsStream("links.json"))
                .assertEquals("count", 2L)
                .assertThat("links.gc:this.href", endsWith("?pageNumber=1&pageSize=2"))
                .assertNotDefined("links.gc:prev")
                .assertNotDefined("links.gc:next")
                .assertThat("rows", collectionWithSize(equalTo(2)));
    }

    @Test
    public void a_document_can_be_expected_not_to_contain_a_path() throws Exception {
        JsonAsserter asserter = with(JSON);
        assertSame(asserter, asserter.assertNotDefined("$.store.bicycle.cool"));
        asserter.assertNotDefined("$.store.book[*].missing");
    }

    @Test
    public void a_value_can_be_asserted_to_be_null() throws Exception {
        with(JSON).assertNull("$.store.bicycle.nullValue");
    }

    @Test
    public void ends_with_evaluates() throws Exception {
        with(JSON).assertThat("$.store.book[0].category", endsWith("nce"));
    }

    @Test
    public void a_defined_path_fails_the_negative_existence_assertion() throws Exception {
        AssertionError error =
                assertThrows(
                        AssertionError.class,
                        () -> with(JSON).assertNotDefined("$.store.bicycle.color"));
        assertTrue(error.getMessage().contains("$.store.bicycle.color"));
    }

    @Test
    public void a_defined_null_value_fails_the_negative_existence_assertion() {
        assertThrows(
                AssertionError.class,
                () -> with(JSON).assertNotDefined("$.store.bicycle.nullValue"));
    }

    @Test
    public void multiple_matches_fail_the_negative_existence_assertion() {
        assertThrows(
                AssertionError.class, () -> with(JSON).assertNotDefined("$.store.book[*].author"));
    }

    @Test
    public void a_path_can_be_asserted_with_matcher() throws Exception {

        with(JSON)
                .assertThat("$.store.bicycle.color", equalTo("red"))
                .assertThat("$.store.bicycle.price", equalTo(19.95D));
    }

    @Test
    public void list_content_can_be_asserted_with_matcher() throws Exception {

        with(JSON)
                .assertThat(
                        "$..book[*].author",
                        hasItems(
                                "Nigel Rees",
                                "Evelyn Waugh",
                                "Herman Melville",
                                "J. R. R. Tolkien"));

        with(JSON)
                .assertThat(
                        "$..author",
                        hasItems(
                                "Nigel Rees",
                                "Evelyn Waugh",
                                "Herman Melville",
                                "J. R. R. Tolkien"))
                .assertThat("$..author", is(collectionWithSize(equalTo(4))));
    }

    @Test
    public void list_content_can_be_asserted_with_nested_matcher() throws Exception {
        with(JSON)
                .assertThat(
                        "$..book[*]",
                        hasItems(
                                hasEntry("author", "Nigel Rees"),
                                hasEntry("author", "Evelyn Waugh")));
    }

    @Test
    public void map_content_can_be_asserted_with_matcher() throws Exception {

        with(JSON)
                .assertThat("$.store.book[0]", hasEntry("category", "reference"))
                .assertThat("$.store.book[0]", hasEntry("title", "Sayings of the Century"))
                .and()
                .assertThat("$..book[0]", hasItems(hasEntry("category", "reference")))
                .and()
                .assertThat("$.store.book[0]", mapContainingKey(equalTo("category")))
                .and()
                .assertThat("$.store.book[0]", mapContainingValue(equalTo("reference")));

        with(JSON)
                .assertThat("$.['store'].['book'][0]", hasEntry("category", "reference"))
                .assertThat("$.['store'].['book'][0]", hasEntry("title", "Sayings of the Century"))
                .and()
                .assertThat("$..['book'][0]", hasItems(hasEntry("category", "reference")))
                .and()
                .assertThat("$.['store'].['book'][0]", mapContainingKey(equalTo("category")))
                .and()
                .assertThat("$.['store'].['book'][0]", mapContainingValue(equalTo("reference")));
    }

    @Test
    public void an_empty_collection() throws Exception {
        with(JSON).assertThat("$.store.book[?(@.category = 'x')]", emptyCollection());
    }

    @Test
    public void a_path_can_be_asserted_equal_to() throws Exception {

        with(JSON)
                .assertEquals("$.store.book[0].title", "Sayings of the Century")
                .assertThat("$.store.book[0].title", equalTo("Sayings of the Century"));
    }

    @Test
    public void no_hit_returns_null() throws Exception {
        with(JSON).assertThat("$.store.book[1000].title", nullValue());
    }

    @Test
    public void invalid_path() throws Exception {
        with(JSON).assertThat("$.store.book[*].fooBar", emptyCollection());
    }

    private InputStream getResourceAsStream(String resourceName) {
        return getClass().getClassLoader().getResourceAsStream(resourceName);
    }
}
