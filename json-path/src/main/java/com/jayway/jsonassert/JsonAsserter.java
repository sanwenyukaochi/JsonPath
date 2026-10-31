package com.jayway.jsonassert;

import org.hamcrest.Matcher;

public interface JsonAsserter {

    JsonPath reader();

    <T> JsonAsserter assertThat(String path, Matcher<T> matcher);

    <T> JsonAsserter assertEquals(String path, T expected);

    JsonAsserter assertNull(String path);

    <T> JsonAsserter assertNotNull(String path);

    JsonAsserter and();
}
