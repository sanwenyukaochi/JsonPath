package com.jayway.jsonpath;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

public class IssuesTest {
    @Test
    public void issue_7() throws Exception {

        String json =
                """
                {
                    "foo": [
                        { "id": 1 },
                        { "id": 2 },
                        { "id": 3 }
                    ]
                }\
                """;

        assertNull(JsonPath.read(json, "$.foo.id"));
    }

    @Test
    public void issue_11() throws Exception {
        String json =
                """
                { "foo" : [] }\
                """;
        List<String> result = JsonPath.read(json, "$.foo[?(@.rel= 'item')][0].uri");

        System.out.println(JsonPath.compile("$.foo[?(@.rel= 'item')][0].uri").isPathDefinite());
        System.out.println(JsonPath.compile("$.foo[?(@.rel= 'item')][0]").isPathDefinite());
        System.out.println(JsonPath.compile("$.foo[?(@.rel= 'item')]").isPathDefinite());

        assertTrue(result.isEmpty());
    }
}
