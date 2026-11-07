package com.jayway.jsonpath;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jayway.jsonpath.spi.JsonProviderFactory;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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

    @Test
    public void a_json_document_can_be_fetched_with_a_URL(@TempDir Path directory)
            throws Exception {
        Path response = directory.resolve("response.json");
        Files.writeString(
                response,
                """
                {"status":"REQUEST_DENIED"}\
                """);
        URL url = response.toUri().toURL();
        assertEquals("REQUEST_DENIED", JsonModel.create(url).get("status"));
    }

    @Test
    public void a_json_document_can_be_fetched_with_a_InputStream() throws Exception {
        ByteArrayInputStream bis = new ByteArrayInputStream(DOCUMENT.getBytes());
        assertEquals("Nigel Rees", JsonModel.create(bis).get("store.book[0].author"));
    }

    @Test
    public void test_a_sub_model_can_be_fetched_and_read() throws Exception {
        JsonModel model = JsonModel.create(DOCUMENT);
        assertEquals("Nigel Rees", model.getSubModel("$store.book[0]").get("author"));
        assertEquals(
                "Nigel Rees", model.getSubModel(JsonPath.compile("$store.book[0]")).get("author"));
    }

    @Test
    public void maps_and_list_can_queried() throws Exception {
        Map<String, Object> doc = new HashMap<String, Object>();
        doc.put("items", asList(0, 1, 2));
        doc.put("child", Collections.singletonMap("key", "value"));

        JsonModel model = JsonModel.create(doc);

        assertEquals("value", model.get("$child.key"));
        assertEquals(1, model.<Integer>get("$items[1]"));
        assertEquals(doc, JsonProviderFactory.getInstance().parse(model.toJson()));
    }

    @Test
    public void map_a_json_model() throws Exception {

        JsonModel model = JsonModel.create(DOCUMENT);

        List<Book> booksList = model.map("$.store.book[0,1]").toListOf(Book.class);

        Set<Book> bookSet = model.map("$.store.book[0,1]").toSetOf(Book.class);

        Book book = model.map("$.store.book[1]").to(Book.class);

        assertEquals("fiction", book.category);
        assertEquals("Evelyn Waugh", book.author);
        assertEquals("Sword of Honour", book.title);
        assertEquals(12.99D, book.price);

        List<Book> booksList2 = model.map("$.store.book[*]").toListOf(Book.class);

        List<Book> booksList3 = model.map("$.store.book[*]").toList().of(Book.class);

        System.out.println("asd");
    }

    public static class Book {
        public String category;
        public String author;
        public String title;
        public String isbn;
        public Double price;
    }
}
