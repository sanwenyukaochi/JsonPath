package com.jayway.jsonpath;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jayway.jsonpath.internal.PathTokenizer;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;

public class PathTest {

    /*
    1.  "$..book[-1:].foo.bar"
    2.  "$.store.book[*].author"
    3.  "$..author"
    4.  "$.store.*"
    5.  "$.store..price"
    6.  "$..book[(@.length-1)]"
    7.  "$..book[-1:]
    8.  "$..book[0,1]"
    9.  "$..book[:2]"
    10. "$..book[?(@.isbn)]"
    11. "$..book[?(@.price<10)]"
    12. "$..*"
    */

    Filter filter =
            new Filter() {
                @Override
                public boolean accept(Object obj) {
                    return true;
                }

                @Override
                public Filter addCriteria(Criteria criteria) {
                    return this;
                }
            };

    @Test
    public void path_is_not_definite() throws Exception {
        assertFalse(JsonPath.compile("$..book[0]").isPathDefinite());
        assertFalse(JsonPath.compile("$book[?]", filter).isPathDefinite());
        assertFalse(JsonPath.compile("$.books[*]").isPathDefinite());
    }

    @Test
    public void path_is_definite() throws Exception {
        assertTrue(JsonPath.compile("$.definite.this.is").isPathDefinite());
        assertTrue(JsonPath.compile("rows[0].id").isPathDefinite());
    }

    @Test
    public void valid_path_is_split_correctly() throws Exception {

        assertPath("$.store[*]", hasItems("$", "store", "[*]"));

        assertPath("$", hasItems("$"));

        assertPath("$..*", hasItems("$", "..", "*"));

        assertPath("$.store", hasItems("$", "store"));

        assertPath("$.store.*", hasItems("$", "store", "*"));

        assertPath("$.store[*].name", hasItems("$", "store", "[*]", "name"));

        assertPath("$..book[-1:].foo.bar", hasItems("$", "..", "book", "[-1:]", "foo", "bar"));

        assertPath("$..book[?(@.isbn)]", hasItems("$", "..", "book", "[?(@.isbn)]"));

        assertPath("['store'].['price']", hasItems("$", "store", "price"));

        assertPath("$.['store'].['price']", hasItems("$", "store", "price"));

        assertPath("$.['store']['price']", hasItems("$", "store", "price"));

        assertPath("$.['store'].price", hasItems("$", "store", "price"));

        assertPath("$.['store space']['price space']", hasItems("$", "store space", "price space"));

        assertPath("$.['store']['nice.price']", hasItems("$", "store", "nice.price"));

        assertPath("$..book[?(@.price<10)]", hasItems("$", "..", "book", "[?(@.price<10)]"));

        assertPath("$..book[?(@.price<10)]", hasItems("$", "..", "book", "[?(@.price<10)]"));

        assertPath("$.store.book[*].author", hasItems("$", "store", "book", "[*]", "author"));

        assertPath("$.store..price", hasItems("$", "store", "..", "price"));
    }

    @Test
    public void white_space_are_removed() throws Exception {

        assertPath("$.[ 'store' ]", hasItems("$", "store"));

        assertPath("$.[   'store' ]", hasItems("$", "store"));

        assertPath("$.['store bore']", hasItems("$", "store bore"));

        assertPath("$..book[  ?(@.price<10)  ]", hasItems("$", "..", "book", "[?(@.price<10)]"));

        assertPath("$..book[?(@.price<10  )]", hasItems("$", "..", "book", "[?(@.price<10)]"));

        assertPath("$..book[?(  @.price<10)]", hasItems("$", "..", "book", "[?(@.price<10)]"));

        assertPath("$..book[  ?(@.price<10)]", hasItems("$", "..", "book", "[?(@.price<10)]"));
    }

    @Test
    public void dot_ending_ignored() throws Exception {

        assertPath("$..book['something'].", hasItems("$", "..", "something"));
    }

    @Test
    public void invalid_path_throws_exception() throws Exception {
        assertPathInvalid("$...*");
    }

    @Test
    public void multi_field_select() {
        PathTokenizer tokenizer = new PathTokenizer("$.contents[*].['groupType', 'type']");
        for (String fragment : tokenizer.getFragments()) {
            System.out.println(fragment);
        }
    }

    private void assertPathInvalid(String path) {
        try {
            PathTokenizer tokenizer = new PathTokenizer(path);
            assertTrue(false, "Expected exception!");
        } catch (InvalidPathException expected) {
        }
    }

    private void assertPath(String path, Matcher<Iterable<String>> matcher) {
        System.out.println("PATH: " + path);
        PathTokenizer tokenizer = new PathTokenizer(path);
        for (String fragment : tokenizer.getFragments()) {
            System.out.println(fragment);
        }
        assertThat(tokenizer.getFragments(), matcher);
        System.out.println("----------------------------------");
    }
}
