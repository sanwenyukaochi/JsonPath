package com.jayway.jsonpath;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import org.junit.jupiter.api.Test;

public class PathUtilTest {

    @Test
    public void quoted_properties_are_split_into_plain_property_fragments() {
        assertEquals(
                List.of("$", "store", "book", "[*]", "author"),
                PathUtil.splitPath("$.['store'].['book'][*].['author']"));
    }

    @Test
    public void wildcard_is_not_definite() throws Exception {
        assertFalse(PathUtil.isPathDefinite("$..book[0]"));
    }
}
