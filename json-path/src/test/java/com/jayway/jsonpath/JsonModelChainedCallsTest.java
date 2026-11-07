package com.jayway.jsonpath;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class JsonModelChainedCallsTest {

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
                        "number": 12,
                        "dot.notation": "new"
                    }
                }
            }\
            """;

    @Test
    public void convert_and_map() throws Exception {

        JsonModel model = JsonModel.model(DOCUMENT);

        Transformer<Map<String, Object>> transformer =
                new Transformer<Map<String, Object>>() {
                    @Override
                    public Object transform(Map<String, Object> map, Configuration configuration) {
                        map.remove("isbn");
                        map.put("author", "kalle");
                        return map;
                    }
                };

        Book book = model.opsForObject("store.book[0]").transform(transformer).to(Book.class);

        assertEquals(book.author, "kalle");
    }

    @Test
    public void convert_each_and_map() throws Exception {

        JsonModel model = JsonModel.model(DOCUMENT);

        Transformer<Object> transformer =
                new Transformer<Object>() {
                    @Override
                    public Object transform(Object obj, Configuration configuration) {
                        Map<String, Object> map = (Map<String, Object>) obj;
                        map.remove("isbn");
                        map.put("author", "kalle");
                        return map;
                    }
                };

        List<Book> books =
                model.opsForArray("store.book").each(transformer).toList().of(Book.class);

        System.out.println();
    }

    public static class Book {
        public String category;
        public String author;
        public String title;
        public Double price;
    }
}
