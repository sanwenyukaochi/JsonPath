package com.jayway.jsonpath;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class JsonModelSubModelTest {

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
                  "foo:bar": "fooBar",
                  "dot.notation": "new",
                  "book": { "category": "fiction",
                             "author": "J. R. R. Tolkien",
                             "title": "The Lord of the Rings",
                             "isbn": "0-395-19395-8",
                             "price": 22.99
                            }
                  }
                }
              }
            }\
            """;

    @Test
    public void a_sub_model_path_must_be_definite() throws Exception {
        assertThrows(
                IllegalArgumentException.class,
                () -> JsonModel.model(DOCUMENT).getSubModel("$store.book[*]"));
    }

    @Test
    public void test_a_sub_model_can_be_fetched_and_read() throws Exception {
        JsonModel model = JsonModel.model(DOCUMENT);
        assertEquals("Nigel Rees", model.getSubModel("$store.book[0]").get("author"));
        assertEquals(
                "Nigel Rees", model.getSubModel(JsonPath.compile("$store.book[0]")).get("author"));
    }

    @Test
    public void when_a_sub_model_is_updated_the_master_model_is_updated() throws Exception {

        JsonModel model = JsonModel.model(DOCUMENT);

        JsonModel subModel = model.getSubModel("store.book[0]");
        subModel.opsForObject().put("author", "kalle");

        assertEquals("kalle", model.get("store.book[0].author"));
    }

    @Test
    public void when_a_sub_model_root_is_transformed_the_master_model_is_updated()
            throws Exception {

        JsonModel model = JsonModel.model(DOCUMENT);

        JsonModel subModel = model.getSubModel("store.book[0]");
        subModel.opsForObject()
                .transform(
                        new Transformer<Map<String, Object>>() {
                            @Override
                            public Object transform(Map<String, Object> obj) {
                                return Collections.singletonMap("prop", "new");
                            }
                        });
        assertEquals("new", model.get("store.book[0].prop"));
    }

    @Test
    public void when_a_sub_model_child_is_transformed_the_master_model_is_updated()
            throws Exception {

        JsonModel model = JsonModel.model(DOCUMENT);

        JsonModel subModel = model.getSubModel("store.bicycle.book");
        subModel.opsForObject()
                .transform(
                        new Transformer<Map<String, Object>>() {
                            @Override
                            public Object transform(Map<String, Object> obj) {
                                return Collections.singletonMap("prop", "new");
                            }
                        });
        assertEquals("new", model.get("store.bicycle.book.prop"));
    }
}
