package com.jayway.jsonpath;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jayway.jsonpath.reader.PathToken;
import com.jayway.jsonpath.reader.PathTokenizer;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.impl.DefaultJsonProvider;
import java.util.List;
import org.junit.jupiter.api.Test;

public class PathTokenizerTest {

    private JsonProvider jsonProvider = new DefaultJsonProvider();

    public static final String DOCUMENT =
            """
            {
                "store": {
                    "book": [
                        {
                            "category": "reference",
                            "author": "Nigel Rees",
                            "title": "Sayings of the Century",
                            "price": 8.95
                        },
                        {
                            "category": "fiction",
                            "author": "Evelyn Waugh",
                            "title": "Sword of Honour",
                            "price": 12.99
                        },
                        {
                            "category": "fiction",
                            "author": "Herman Melville",
                            "title": "Moby Dick",
                            "isbn": "0-553-21311-3",
                            "price": 8.99
                        },
                        {
                            "category": "fiction",
                            "author": "J. R. R. Tolkien",
                            "title": "The Lord of the Rings",
                            "custom": "onely this",
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

    @Test
    public void path_tokens_can_be_read() throws Exception {
        Object result = jsonProvider.parse(DOCUMENT);

        for (PathToken pathToken : new PathTokenizer("$.store.bicycle.color", jsonProvider)) {
            result = pathToken.filter(result, jsonProvider);
        }

        assertEquals("red", result);
    }

    @Test
    public void read_an_array_without_filters() throws Exception {
        Object result = jsonProvider.parse(DOCUMENT);

        for (PathToken pathToken : new PathTokenizer("$.store.book", jsonProvider)) {
            result = pathToken.filter(result, jsonProvider);
        }

        assertEquals(4, toList(result).size());
    }

    @Test
    public void read_a_literal_property_from_object_in_array() throws Exception {
        Object result = jsonProvider.parse(DOCUMENT);

        for (PathToken pathToken : new PathTokenizer("$.store.book[*].title", jsonProvider)) {
            result = pathToken.filter(result, jsonProvider);
        }

        assertEquals(4, toList(result).size());
    }

    @Test
    public void read_a_literal_property_from_position_in_array() throws Exception {
        Object result = jsonProvider.parse(DOCUMENT);

        for (PathToken pathToken : new PathTokenizer("$.store.book[0].title", jsonProvider)) {
            result = pathToken.filter(result, jsonProvider);
        }

        assertEquals("Sayings of the Century", result);
    }

    @Test
    public void read_a_literal_property_from_two_positions_in_array() throws Exception {
        Object result = jsonProvider.parse(DOCUMENT);

        for (PathToken pathToken : new PathTokenizer("$.store.book[0, 1].author", jsonProvider)) {
            result = pathToken.filter(result, jsonProvider);
        }

        assertThat(this.<String>toList(result), hasItems("Nigel Rees", "Evelyn Waugh"));
    }

    @Test
    public void read_a_literal_property_from_head_in_array() throws Exception {
        Object result = jsonProvider.parse(DOCUMENT);

        for (PathToken pathToken : new PathTokenizer("$.store.book[:2].author", jsonProvider)) {
            result = pathToken.filter(result, jsonProvider);
        }

        assertThat(this.<String>toList(result), hasItems("Nigel Rees", "Evelyn Waugh"));
    }

    @Test
    public void read_a_literal_property_from_tail_in_array() throws Exception {
        Object result = jsonProvider.parse(DOCUMENT);

        for (PathToken pathToken : new PathTokenizer("$.store.book[-1:].author", jsonProvider)) {
            result = pathToken.filter(result, jsonProvider);
        }
        assertEquals("J. R. R. Tolkien", result);
    }

    @Test
    public void field_defined_in_array_object() throws Exception {
        Object result = jsonProvider.parse(DOCUMENT);

        for (PathToken pathToken :
                new PathTokenizer("$.store.book[?(@.custom)].author", jsonProvider)) {
            result = pathToken.filter(result, jsonProvider);
        }
        assertThat(this.<String>toList(result), hasItems("J. R. R. Tolkien"));
    }

    @Test
    public void property_value_in_array_object() throws Exception {
        Object result = jsonProvider.parse(DOCUMENT);

        for (PathToken pathToken :
                new PathTokenizer(
                        "$.store.book[?(@.custom = 'onely this')].author", jsonProvider)) {
            result = pathToken.filter(result, jsonProvider);
        }
        assertThat(this.<String>toList(result), hasItems("J. R. R. Tolkien"));
    }

    @Test
    public void deep_scan() throws Exception {
        Object result = jsonProvider.parse(DOCUMENT);

        for (PathToken pathToken : new PathTokenizer("$..author", jsonProvider)) {
            result = pathToken.filter(result, jsonProvider);
        }
        assertThat(
                this.<String>toList(result),
                hasItems("Nigel Rees", "Evelyn Waugh", "J. R. R. Tolkien"));
    }

    private <T> List<T> toList(Object obj) {
        return (List<T>) obj;
    }
}
