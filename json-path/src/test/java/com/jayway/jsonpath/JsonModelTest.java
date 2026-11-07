package com.jayway.jsonpath;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jayway.jsonpath.spi.JsonProviderFactory;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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

    public static final String INVALID_DOCUMENT =
            """
            {?\\?\\?!!?~q`}}}}}"" "store": {
            """;

    @Test
    public void invalid_json_throws() throws Exception {
        assertThrows(
                InvalidJsonException.class,
                () -> JsonModel.model(INVALID_DOCUMENT).get("store.id"));
    }

    @Test
    public void invalid_path_throws() throws Exception {
        assertThrows(InvalidPathException.class, () -> JsonModel.model(DOCUMENT).get("a("));
    }

    @Test
    public void a_model_can_be_pretty_printed() throws Exception {
        JsonModel model = JsonModel.model(DOCUMENT);

        model.print();
    }

    @Test
    public void has_path_validates() throws Exception {
        assertFalse(JsonModel.model(DOCUMENT).hasPath("store.invalid"));
        assertFalse(JsonModel.model(DOCUMENT).hasPath("store.book[0].foo"));

        assertTrue(JsonModel.model(DOCUMENT).hasPath("store.book"));
        assertTrue(JsonModel.model(DOCUMENT).hasPath("store.book[0].title"));
    }

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
        assertEquals("REQUEST_DENIED", JsonModel.model(url).get("status"));
    }

    @Test
    public void a_json_document_can_be_fetched_with_a_InputStream() throws Exception {
        ByteArrayInputStream bis = new ByteArrayInputStream(DOCUMENT.getBytes());
        assertEquals("Nigel Rees", JsonModel.model(bis).get("store.book[0].author"));
    }

    @Test
    public void maps_and_list_can_queried() throws Exception {
        Map<String, Object> doc = new HashMap<String, Object>();
        doc.put("items", asList(0, 1, 2));
        doc.put("child", Collections.singletonMap("key", "value"));

        JsonModel model = JsonModel.model(doc);

        assertEquals("value", model.get("$child.key"));
        assertEquals(1, model.<Integer>get("$items[1]"));
        assertEquals(doc, JsonProviderFactory.createProvider().parse(model.toJson()));
    }

    @Test
    public void query_for_null_property_returns_null() {
        String documentWithNull =
                """
                {
                    "store": {
                        "book": {
                            "color": null
                        }
                    }
                }\
                """;

        Object color = JsonModel.model(documentWithNull).get("store.book.color");

        assertNull(color);
    }

    @Test
    public void query_for_property_on_array_throws() throws Exception {
        assertThrows(
                InvalidPathException.class,
                () -> JsonModel.model(DOCUMENT).get("store.book.color"));
    }
}
