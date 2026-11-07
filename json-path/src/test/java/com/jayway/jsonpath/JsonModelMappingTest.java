package com.jayway.jsonpath;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;
import static com.jayway.jsonpath.JsonModel.model;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class JsonModelMappingTest {

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
    public void map_and_filter_can_be_combined() throws Exception {

        JsonModel model = JsonModel.model(DOCUMENT);

        Filter filter =
                Filter.filter(Criteria.where("category").is("fiction").and("price").gt(10D));

        List<Book> books = model.map("$.store.book[?]", filter).toList().of(Book.class);

        JsonPath jsonPath =
                JsonPath.compile(
                        "$.store.book[?]",
                        filter(where("category").is("fiction").and("price").gt(10D)));

        Object read = jsonPath.read(DOCUMENT);

        List<Book> books1 =
                JsonModel.model(DOCUMENT).map("$.store.book[?]", filter).toListOf(Book.class);

        model(DOCUMENT);

        System.out.println("");
    }

    @Test
    public void map_a_json_model_to_an_Class() throws Exception {

        JsonModel model = JsonModel.model(DOCUMENT);

        Book book = model.map("$.store.book[1]").to(Book.class);

        assertEquals("fiction", book.category);
        assertEquals("Evelyn Waugh", book.author);
        assertEquals("Sword of Honour", book.title);
        assertEquals(12.99D, book.price);
    }

    @Test
    public void map_a_json_model_to_a_List() throws Exception {
        JsonModel model = JsonModel.model(DOCUMENT);

        List<Book> booksList = model.map("$.store.book[0,1]").toListOf(Book.class);

        assertEquals("fiction", booksList.get(1).category);
        assertEquals("Evelyn Waugh", booksList.get(1).author);
        assertEquals("Sword of Honour", booksList.get(1).title);
        assertEquals(12.99D, booksList.get(1).price);

        booksList = model.map("$.store.book[*]").toListOf(Book.class);

        assertEquals("fiction", booksList.get(1).category);
        assertEquals("Evelyn Waugh", booksList.get(1).author);
        assertEquals("Sword of Honour", booksList.get(1).title);
        assertEquals(12.99D, booksList.get(1).price);

        booksList = model.map("$.store.book[*]").toList().of(Book.class);

        assertEquals("fiction", booksList.get(1).category);
        assertEquals("Evelyn Waugh", booksList.get(1).author);
        assertEquals("Sword of Honour", booksList.get(1).title);
        assertEquals(12.99D, booksList.get(1).price);
    }

    @Test
    public void map_a_json_model_to_a_Set() throws Exception {

        JsonModel model = JsonModel.model(DOCUMENT);

        Set<Book> bookSet = model.map("$.store.book[1]").toSetOf(Book.class);

        Book book = bookSet.iterator().next();

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
