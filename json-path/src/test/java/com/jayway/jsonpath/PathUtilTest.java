package com.jayway.jsonpath;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class PathUtilTest {

    @Test
    public void wildcard_is_not_definite() throws Exception {
        assertFalse(PathUtil.isPathDefinite("$..book[0]"));
    }

    @Test
    public void is_definite() throws Exception {
        assertTrue(PathUtil.isPathDefinite("$.definite.this.is"));
        assertTrue(PathUtil.isPathDefinite("$.definite:this.is"));
        assertTrue(PathUtil.isPathDefinite("rows[0].id"));
    }
}
