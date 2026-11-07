package com.jayway.jsonpath;

import com.jayway.jsonpath.spi.JsonProviderFactory;

import org.junit.jupiter.api.Test;

public class JsonModelTest {

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

    public static final JsonModel MODEL = new JsonModel(DOCUMENT);

    @Test
    public void a_path_can_be_read() throws Exception {

        JsonPath path = JsonPath.compile("$.store.book[*].title");

        JsonModel model = new JsonModel(DOCUMENT);

        Object result = model.get(path);

        System.out.println(JsonProviderFactory.getInstance().toJson(result));
    }

    @Test
    public void test2() throws Exception {
        JsonPath path = JsonPath.compile("$..");

        JsonModel model = new JsonModel(DOCUMENT);

        System.out.println(model.getJson(path));
    }

    @Test
    public void test3() throws Exception {
        JsonPath path = JsonPath.compile("$..[0]");

        System.out.println(MODEL.getJson(path));
    }

    @Test
    public void test4() throws Exception {
        JsonPath path = JsonPath.compile("$..*");

        System.out.println("--------------------------------");

        JsonModel model = new JsonModel(DOCUMENT);
        System.out.println(model.getJson(path));
    }

    @Test
    public void test5() throws Exception {

        JsonModel model = new JsonModel(DOCUMENT);

        JsonModel model2 = model.getSubModel("store.book[0]");

        System.out.println(model2.getJson());
    }
}
