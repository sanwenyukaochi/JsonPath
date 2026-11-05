package com.jayway.jsonassert;

import org.hamcrest.Matcher;

public interface JsonAsserter {

    <T> JsonAsserter assertThat(String path, Matcher<T> matcher);

    <T> JsonAsserter assertEquals(String path, T expected);

    JsonAsserter assertNotDefined(String path);

    JsonAsserter assertNull(String path);

    <T> JsonAsserter assertNotNull(String path);

    JsonAsserter and();
}
