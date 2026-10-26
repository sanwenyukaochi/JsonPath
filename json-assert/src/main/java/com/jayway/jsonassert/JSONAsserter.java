package com.jayway.jsonassert;

import org.hamcrest.Matcher;

public interface JSONAsserter {

    JSONReader reader();

    <T> JSONAsserter assertThat(String path, Matcher<T> matcher);

    <T> JSONAsserter assertEquals(String path, T expected);

    JSONAsserter assertNull(String path);

    <T> JSONAsserter assertNotNull(String path);

    JSONAsserter and();
}
