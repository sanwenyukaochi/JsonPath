package com.jayway.jsonassert;

import com.jayway.jsonassert.impl.JsonPathImpl;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class JsonPathSpecTest {

    public final static String DOCUMENT =
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
    public void the_authors_of_all_books_in_the_store() throws Exception {
        JsonPath reader = JsonPathImpl.parse(DOCUMENT);

        assertThat(reader.<String>getList("store.book[*].author"), hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
    }

    @Test
    public void all_authors() throws Exception {

        JsonPath reader = JsonPathImpl.parse(DOCUMENT);

        assertThat(reader.<String>getList("..author"), hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));

    }

    @Test
    public void all_items_in_store() throws Exception {
        JsonPath reader = JsonPathImpl.parse(DOCUMENT);

        List<Object> itemsInStore = reader.<Object>getList("store.*");


        assertEquals(JsonPathImpl.jsonPath(itemsInStore, "[0][0].author"), "Nigel Rees");

        System.out.println(itemsInStore.toString());
    }

    @Test
    public void the_price_of_everything_in_the_store() throws Exception {
        JsonPath reader = JsonPathImpl.parse(DOCUMENT);

        assertThat(reader.<Double>getList("store..price"), hasItems(8.95D, 12.99D, 8.99D, 19.95D));
    }

    @Test
    public void the_third_book() throws Exception {
        JsonPath reader = JsonPathImpl.parse(DOCUMENT);

        Map<String, String> book = reader.getReader("..book[2]").get("[0]");

        assertThat(book, hasEntry("author", "Herman Melville"));


        book = reader.getMap("store.book[2]");

        assertThat(book, hasEntry("author", "Herman Melville"));
    }


}
