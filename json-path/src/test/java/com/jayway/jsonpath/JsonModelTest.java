package com.jayway.jsonpath;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        assertEquals("Nigel Rees", model.getModel("$store.book[0]").get("author"));
        assertEquals(
                "Nigel Rees", model.getModel(JsonPath.compile("$store.book[0]")).get("author"));
    }

    @Test
    public void maps_and_list_can_queried() throws Exception {
        Map<String, Object> doc = new HashMap<String, Object>();
        doc.put("items", asList(0, 1, 2));
        doc.put("child", Collections.singletonMap("key", "value"));

        JsonModel model = JsonModel.create(doc);

        assertEquals("value", model.get("$child.key"));
        assertEquals(1, model.<Integer>get("$items[1]"));
        assertEquals(doc, JsonProviderFactory.createProvider().parse(model.getJson()));
    }

    @Test
    public void invalid_path_throws() throws Exception {
        assertThrows(
                InvalidPathException.class, () -> JsonModel.create(DOCUMENT).get("store.invalid"));
    }
}
