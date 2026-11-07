package com.jayway.jsonpath;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Issue_762 {
    @Test
    public void testParseJsonValue() {
        Assertions.assertTrue(JsonPath.parse(5).jsonString().equals("5"));
        Assertions.assertTrue(JsonPath.parse(5.0).jsonString().equals("5.0"));
        Assertions.assertTrue(JsonPath.parse(true).jsonString().equals("true"));
        Assertions.assertTrue(JsonPath.parse(false).jsonString().equals("false"));
    }
}
