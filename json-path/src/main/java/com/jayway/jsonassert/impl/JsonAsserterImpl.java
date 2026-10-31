package com.jayway.jsonassert.impl;

import com.jayway.jsonassert.JsonAsserter;
import com.jayway.jsonassert.JsonPath;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

import static org.hamcrest.Matchers.*;

public class JsonAsserterImpl implements JsonAsserter {

    private final JsonPath reader;

    public JsonAsserterImpl(JsonPath reader) {
        this.reader = reader;
    }

    public JsonPath reader() {
        return reader;
    }

    @SuppressWarnings("unchecked")
	public <T> JsonAsserter assertThat(String path, Matcher<T> matcher) {
        MatcherAssert.assertThat((T) reader.get(path), matcher);
        return this;
    }

    public <T> JsonAsserter assertEquals(String path, T expected) {
        return assertThat(path, equalTo(expected));
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
