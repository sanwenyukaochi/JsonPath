package com.jayway.jsonpath;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class MultiAttributeTest {

    public static final String DOCUMENT =
            """
            { "store": {
                "book": [
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

    @Test
    public void multiple_attributes_from_array_content() {
        List<Map<String, Object>> matches =
                JsonPath.read(DOCUMENT, "$.store.book[*].['category', 'title']");

        assertEquals(4, matches.size());
        assertTrue(matches.get(1).containsKey("category"));
        assertTrue(matches.get(1).containsKey("title"));
        assertEquals(2, matches.get(1).size());
    }

    @Test
    public void multiple_attributes_from_single_object() {
        Map<String, Object> match =
                JsonPath.read(DOCUMENT, "$.store.bicycle['color', 'display-price']");

        assertTrue(match.containsKey("color"));
        assertTrue(match.containsKey("display-price"));
    }
}
