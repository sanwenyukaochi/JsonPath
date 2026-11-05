package com.jayway.jsonpath;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    public void paths_with_colons_in_property_names_are_definite() {
        assertTrue(PathUtil.isPathDefinite("$.definite.this.is"));
        assertTrue(PathUtil.isPathDefinite("$.definite:this.is"));
        assertTrue(PathUtil.isPathDefinite("$.['definite:this'].['is']"));
        assertTrue(PathUtil.isPathDefinite("rows[0].id"));
    }

    @Test
    public void array_slices_and_tail_selectors_are_not_definite() {
        assertFalse(PathUtil.isPathDefinite("$.store.book[:2]"));
        assertFalse(PathUtil.isPathDefinite("$.store.book[-1:]"));
        assertFalse(PathUtil.isPathDefinite("$.store.book[ :2 ]"));
        assertFalse(PathUtil.isPathDefinite("$.store.book[-1: ]"));
    }
}
