package com.jayway.jsonpath;

import static com.jayway.jsonpath.JsonModel.model;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class JsonModelSubModelDetachedTest {

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
    public void a_sub_model_can_be_detached() throws Exception {

        JsonModel model = model(DOCUMENT);

        JsonModel detachedModel = model.getSubModelDetached("$.store.book[*]");

        detachedModel.opsForArray().add(1);

        assertEquals(4, model.opsForArray("$.store.book").size());
        assertEquals(5, detachedModel.opsForArray("$.store.book").size());
    }
}
