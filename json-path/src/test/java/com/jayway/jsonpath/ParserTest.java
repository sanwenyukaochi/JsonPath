package com.jayway.jsonpath;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.ParseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class ParserTest {

    private static final String SINGLE_QUOTE_JSON =
            """
            {'lhs': '1.0 U.S. dollar','rhs': 6.39778892, 'error': null, 'icc': true}\
            """;
    private static final String NO_QUOTE_JSON =
            """
            {lhs: 1.0 U.S. dollar, rhs: 6.39778892, error: null, icc: true}\
            """;

    @AfterEach
    public void restore_slack_mode() {
        JsonPath.setMode(JsonPath.SLACK_MODE);
    }

    @Test
    public void default_mode_is_SLACK_MODE() throws Exception {
        assertEquals(JsonPath.SLACK_MODE, JsonPath.getMode());
    }

    @Test
    public void slack_mode_allows_single_quotes() throws Exception {
        assertEquals(JsonPath.read(SINGLE_QUOTE_JSON, "lhs"), "1.0 U.S. dollar");
        assertEquals(JsonPath.<Double>read(SINGLE_QUOTE_JSON, "rhs"), 6.39778892D);
    }

    @Test
    public void slack_mode_allows_no_quotes() throws Exception {
        assertEquals(JsonPath.read(NO_QUOTE_JSON, "lhs"), "1.0 U.S. dollar");
        assertEquals(JsonPath.<Double>read(NO_QUOTE_JSON, "rhs"), 6.39778892D);
    }

    @Test
    public void strict_mode_does_not_accept_single_quotes() throws Exception {
        JsonPath.setMode(JsonPath.STRICT_MODE);
        assertThrows(ParseException.class, () -> JsonPath.read(SINGLE_QUOTE_JSON, "lhs"));
    }

    @Test
    public void strict_mode_does_not_accept_no_quotes() throws Exception {
        JsonPath.setMode(JsonPath.STRICT_MODE);
        assertThrows(ParseException.class, () -> JsonPath.read(NO_QUOTE_JSON, "lhs"));
    }
}
