package com.jayway.jsonpath;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class JsonModelOpsTest {

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
    public void object_ops_can_update() throws Exception {

        JsonModel model = JsonModel.create(DOCUMENT);

        model.opsForObject("store.book[0]").put("author", "Kalle").put("price", 12.30D);

        assertEquals("Kalle", model.get("store.book[0].author"));
        assertEquals(12.30D, model.<Double>get("store.book[0].price"));
    }

    @Test
    public void array_ops_can_add_element() throws Exception {
        JsonModel model = JsonModel.create(DOCUMENT);

        Map<String, Object> newBook = new HashMap<String, Object>();
        newBook.put("category", "reference");
        newBook.put("author", "Kalle");
        newBook.put("title", "JSONPath book");
        newBook.put("isbn", "0-553-21311-34");
        newBook.put("price", 12.10D);

        model.opsForArray("store.book").add(newBook);

        JsonModel subModel = model.getModel("store.book[4]");

        assertEquals("reference", subModel.get("category"));
        assertEquals("Kalle", subModel.get("author"));
        assertEquals("JSONPath book", subModel.get("title"));
        assertEquals("0-553-21311-34", subModel.get("isbn"));
        assertEquals(12.10D, subModel.<Double>get("price"));
    }

    @Test
    public void arrays_can_be_mapped() throws Exception {
        JsonModel model = JsonModel.create(DOCUMENT);

        List<Book> books1 = model.opsForArray("store.book").toList().of(Book.class);
        List<Book> books2 = model.opsForArray("store.book").toListOf(Book.class);
        Set<Book> books3 = model.opsForArray("store.book").toSetOf(Book.class);

        assertEquals(4, books1.size());
        assertEquals(4, books2.size());
        assertEquals(4, books3.size());
    }

    @Test
    public void objects_can_be_mapped() throws Exception {
        JsonModel model = JsonModel.create(DOCUMENT);

        Book book = model.opsForObject("store.book[1]").to(Book.class);

        assertEquals("fiction", book.category);
        assertEquals("Evelyn Waugh", book.author);
        assertEquals("Sword of Honour", book.title);
        assertEquals(12.99D, book.price);
    }

    public static class Book {
        public String category;
        public String author;
        public String title;
        public String isbn;
        public Double price;
    }
}
