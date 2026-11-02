package com.jayway.jsonpath;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class PathUtilTest {


    @Test
    public void wildcard_is_not_definite() throws Exception {
        assertFalse(PathUtil.isPathDefinite("$..book[0]"));
    }


}
