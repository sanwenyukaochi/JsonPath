package com.jayway.jsonassert;

import org.junit.jupiter.api.Test;

import static com.jayway.jsonassert.JsonAssert.with;
import static org.hamcrest.Matchers.*;

public class JsonAssertTest {

    public final static String JSON =
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
                          "price": 19.95
                        }
                      }
                    }""";

    @Test
    public void a_path_can_be_asserted_with_matcher() throws Exception {

        with(JSON).assertThat("$.store.bicycle.color", equalTo("red"))
                  .assertThat("$.store.bicycle.price", equalTo(19.95D));
    }

    @Test
    public void list_content_can_be_asserted_with_matcher() throws Exception {

        with(JSON).assertThat("$..book[*].author", hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));

        with(JSON).assertThat("$..author", hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
    }

    @Test
    public void list_content_can_be_asserted_with_nested_matcher() throws Exception {
        with(JSON).assertThat("$..book[*]", hasItems(hasEntry("author", "Nigel Rees"), hasEntry("author", "Evelyn Waugh")));
    }

    @Test
    public void map_content_can_be_asserted_with_matcher() throws Exception {

        with(JSON).assertThat("$.store.book[0]", hasEntry("category", "reference"))
                  .assertThat("$.store.book[0]", hasEntry("title", "Sayings of the Century"));

        with(JSON).assertThat("$..book[0]", hasItems(hasEntry("category", "reference")));
    }

    @Test
    public void a_path_can_be_asserted_equal_to() throws Exception {

        with(JSON).assertEquals("$.store.book[0].title", "Sayings of the Century")
                  .assertThat("$.store.book[0].title", equalTo("Sayings of the Century"));
    }


    /*
    @Test
    public void a_sub_document_can_asserted_on__by_path() throws Exception {
        JsonAssert.with(TEST_DOCUMENT).assertThat("subDocument.subField", is(equalTo("sub-field")));
    }

    @Test
    public void a_path_can_be_asserted_equal_to() throws Exception {

        JsonAssert.with(TEST_DOCUMENT).assertEquals("stringField", "string-field");
    }

    @Test
    public void a_path_can_be_asserted_is_null() throws Exception {

        JsonAssert.with(TEST_DOCUMENT).assertNull("nullField");
    }

    @Test(expected = AssertionError.class)
    public void failed_assert_throws() throws Exception {

        JsonAssert.with(TEST_DOCUMENT).assertThat("stringField", equalTo("SOME CRAP"));
    }

    @Test
    public void multiple_asserts_can_be_chained() throws Exception {

        JsonAssert.with(TEST_DOCUMENT)
                .assertThat("stringField", equalTo("string-field"))
                .assertThat("numberField", is(notNullValue()))
                .and()
                .assertNull("nullField")
                .and()
                .assertEquals("stringField", "string-field");

    }
    */


}
