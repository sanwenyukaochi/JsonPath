package com.jayway.jsonpath;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jayway.jsonpath.reader.PathTokenizer;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.impl.DefaultJsonProvider;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;

public class SplitPathFragmentsTest {

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

    private JsonProvider jsonProvider = new DefaultJsonProvider();

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
    public void invalid_path_throws_exception() throws Exception {
        assertPathInvalid("$...*");
    }

    private void assertPathInvalid(String path) {
        try {
            PathTokenizer tokenizer = new PathTokenizer(path, jsonProvider);
            assertTrue(false, "Expected exception!");
        } catch (InvalidPathException expected) {
        }
    }

    private void assertPath(String path, Matcher<Iterable<String>> matcher) {
        System.out.println("PATH: " + path);
        PathTokenizer tokenizer = new PathTokenizer(path, jsonProvider);
        for (String fragment : tokenizer.getFragments()) {
            System.out.println(fragment);
        }
        assertThat(tokenizer.getFragments(), matcher);
        System.out.println("----------------------------------");
    }
}
