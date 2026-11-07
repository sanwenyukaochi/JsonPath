package com.jayway.jsonassert.impl;

import static java.lang.String.format;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import com.jayway.jsonassert.JsonAsserter;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import org.hamcrest.Matcher;

public class JsonAsserterImpl implements JsonAsserter {

    private final Object jsonObject;

    public JsonAsserterImpl(Object jsonObject) {
        this.jsonObject = jsonObject;
    }

    @SuppressWarnings("unchecked")
    public <T> JsonAsserter assertThat(String path, Matcher<T> matcher) {
        T obj = JsonPath.<T>read(jsonObject, path);
        if (!matcher.matches(obj)) {

            throw new AssertionError(
                    String.format(
                            "JSON doesn't match.\nExpected:\n%s\nActual:\n%s",
                            matcher.toString(), obj));
        }
        return this;
    }

    public <T> JsonAsserter assertEquals(String path, T expected) {
        return assertThat(path, equalTo(expected));
    }

    public JsonAsserter assertNotDefined(String path) {

        try {
            Object o = JsonPath.read(jsonObject, path);
            throw new AssertionError(
                    format("Document contains the path <%s> but was expected not to.", path));
        } catch (InvalidPathException e) {
        }
        return this;
    }

    public JsonAsserter assertNull(String path) {
        return assertThat(path, nullValue());
    }

    public <T> JsonAsserter assertNotNull(String path) {
        return assertThat(path, notNullValue());
    }

    public JsonAsserter and() {
        return this;
    }
}
