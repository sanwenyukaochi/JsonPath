package com.jayway.jsonassert;

import org.hamcrest.Matcher;

public interface JsonAsserter {

    <T> JsonAsserter assertThat(String path, Matcher<T> matcher);

    <T> JsonAsserter assertThat(String path, Matcher<T> matcher, String message);

    <T> JsonAsserter assertEquals(String path, T expected);

    <T> JsonAsserter assertEquals(String path, T expected, String message);

    JsonAsserter assertNotDefined(String path);

    JsonAsserter assertNotDefined(String path, String message);

    JsonAsserter assertNull(String path);

    JsonAsserter assertNull(String path, String message);

    <T> JsonAsserter assertNotNull(String path);

    <T> JsonAsserter assertNotNull(String path, String message);

    JsonAsserter and();
}
