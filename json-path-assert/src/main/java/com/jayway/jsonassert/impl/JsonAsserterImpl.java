package com.jayway.jsonassert.impl;


import com.jayway.jsonassert.JsonAsserter;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathUtil;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

import java.text.ParseException;
import java.util.List;

import static org.hamcrest.Matchers.*;

public class JsonAsserterImpl implements JsonAsserter {

    private final Object jsonObject;

    public JsonAsserterImpl(Object jsonObject) {
        this.jsonObject = jsonObject;
    }

    @SuppressWarnings("unchecked")
	public <T> JsonAsserter assertThat(String path, Matcher<T> matcher) {

        if(PathUtil.isPathDefinite(path)){
            MatcherAssert.assertThat(JsonPath.<T>readOne(jsonObject, path), matcher);
        }
        else {
           MatcherAssert.assertThat((T) JsonPath.<T>read(jsonObject, path), matcher);
        }
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
