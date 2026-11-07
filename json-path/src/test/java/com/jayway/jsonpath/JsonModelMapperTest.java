package com.jayway.jsonpath;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

public class JsonModelMapperTest {

    public static final String DOCUMENT =
            """
            {
                "store": {
                    "book": [
                        {
                            "category": "reference",
                            "author": "Nigel Rees",
                            "title": "Sayings of the Century",
                            "displayPrice": 8.95
                        },
                        {
                            "category": "fiction",
                            "author": "Evelyn Waugh",
                            "title": "Sword of Honour",
                            "displayPrice": 12.99
                        },
                        {
                            "category": "fiction",
                            "author": "Herman Melville",
                            "title": "Moby Dick",
                            "isbn": "0-553-21311-3",
                            "displayPrice": 8.99
                        },
                        {
                            "category": "fiction",
                            "author": "J. R. R. Tolkien",
                            "title": "The Lord of the Rings",
                            "isbn": "0-395-19395-8",
                            "displayPrice": 22.99
                        }
                    ],
                    "bicycle": {
                        "color": "red",
                        "displayPrice": 19.95,
                        "foo:bar": "fooBar",
                        "dot.notation": "new",
                        "dash-notation": "dashes"
                    }
                }
            }\
            """;

    @Test
    public void map_a_json_model() throws Exception {

        JsonModel model = JsonModel.create(DOCUMENT);

        List<Book> booksList = model.map("$.store.book[0,1]").toListOf(Book.class);

        Set<Book> bookSet = model.map("$.store.book[0,1]").toSetOf(Book.class);

        Book book = model.map("$.store.book[1]").to(Book.class);

        System.out.println("test");
    }

    @Test
    public void a_book_can_be_mapped() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
    }

    public static class Book {
        private String category;
        private String author;
        private String title;
        private Double displayPrice;

        public Book() {}

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Double getDisplayPrice() {
            return displayPrice;
        }

        public void setDisplayPrice(Double displayPrice) {
            this.displayPrice = displayPrice;
        }
    }
}
