package com.jayway.jsonpath;

import com.jayway.jsonpath.spi.impl.JacksonProvider;

import org.junit.jupiter.api.Test;

public class JacksonProviderTest {

    public static final String ARRAY =
            """
            [
                {
                    "value": 1
                },
                {
                    "value": 2
                },
                {
                    "value": 3
                },
                {
                    "value": 4
                }
            ]\
            """;

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
    public void parse_document() throws Exception {

        JacksonProvider provider = new JacksonProvider();

        Object o = provider.parse(DOCUMENT);

        System.out.println(o);
    }

    @Test
    public void parse_array() throws Exception {
        JacksonProvider provider = new JacksonProvider();

        Object o = provider.parse(ARRAY);

        System.out.println(o);
    }
}
