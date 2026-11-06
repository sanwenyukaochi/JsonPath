package com.jayway.jsonpath;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class IsDefinitePathTest {

    @Test
    public void path_is_not_definite() throws Exception {
        assertFalse(JsonPath.compile("$..book[0]").isPathDefinite());
        assertFalse(JsonPath.compile("$.books[*]").isPathDefinite());
    }

    @Test
    public void path_is_definite() throws Exception {
        assertTrue(JsonPath.compile("$.definite.this.is").isPathDefinite());
        assertTrue(JsonPath.compile("rows[0].id").isPathDefinite());
    }
}
