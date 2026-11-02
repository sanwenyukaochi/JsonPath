package com.jayway.jsonpath;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

public class PathUtilTest {

    @Test
    public void wildcard_is_not_definite() throws Exception {
        assertFalse(PathUtil.isPathDefinite("$..book[0]"));
    }
}
