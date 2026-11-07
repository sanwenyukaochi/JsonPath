package com.jayway.jsonpath;

import static org.junit.jupiter.api.Assertions.assertNull;

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
}
