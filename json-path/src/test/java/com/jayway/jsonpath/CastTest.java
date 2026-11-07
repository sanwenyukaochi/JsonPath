package com.jayway.jsonpath;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CastTest {

    public static final String JSON = "{\"sessionID\":7242750700467747000}";

    @Test
    public void result_can_be_cast_to_Long() {
        Long actual = JsonPath.read(JSON, "$.sessionID");
        Long expected = Long.valueOf("7242750700467747000");
        assertEquals(expected, actual);
    }
}
