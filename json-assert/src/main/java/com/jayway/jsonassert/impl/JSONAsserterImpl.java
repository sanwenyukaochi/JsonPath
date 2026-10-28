package com.jayway.jsonassert.impl;


import com.jayway.jsonassert.JSONAsserter;
import com.jayway.jsonassert.JSONReader;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

import java.text.ParseException;

import static org.hamcrest.Matchers.*;

public class JSONAsserterImpl implements JSONAsserter {

    private final JSONReader reader;

    public static JSONAsserter with(String json) throws ParseException {
        return new JSONAsserterImpl(JSONReaderImpl.parse(json));
    }

    public JSONAsserterImpl(JSONReader reader) {
        this.reader = reader;
    }

    public JSONReader reader() {
        return reader;
    }

    public <T> JSONAsserter assertThat(String path, Matcher<T> matcher) {
        MatcherAssert.assertThat((T) reader.get(path), matcher);
        return this;
    }

    public <T> JSONAsserter assertEquals(String path, T expected) {
        return assertThat(path, equalTo(expected));
    }

    public JSONAsserter assertNull(String path) {
        return assertThat(path, nullValue());
    }

    public <T> JSONAsserter assertNotNull(String path) {
        return assertThat(path, notNullValue());
    }

    public JSONAsserter and() {
        return this;
    }

}
