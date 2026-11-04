package com.jayway.jsonassert;

import static com.jayway.jsonassert.JsonAssert.collectionWithSize;
import static com.jayway.jsonassert.JsonAssert.emptyCollection;
import static com.jayway.jsonassert.JsonAssert.mapContainingKey;
import static com.jayway.jsonassert.JsonAssert.mapContainingValue;
import static com.jayway.jsonassert.JsonAssert.with;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

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
                          "price": 19.95
                        }
                      }
                    }""";

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
}
