package com.jayway.jsonassert.impl;

import static java.lang.String.format;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import com.jayway.jsonassert.JsonAsserter;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathUtil;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

public class JsonAsserterImpl implements JsonAsserter {

    private final Object jsonObject;

    public JsonAsserterImpl(Object jsonObject) {
        this.jsonObject = jsonObject;
    }

    @SuppressWarnings("unchecked")
    public <T> JsonAsserter assertThat(String path, Matcher<T> matcher) {
        String reason = "When processing json path: " + path;

        if (PathUtil.isPathDefinite(path)) {
            MatcherAssert.assertThat(reason, JsonPath.<T>readOne(jsonObject, path), matcher);
        } else {
            MatcherAssert.assertThat(reason, (T) JsonPath.<T>read(jsonObject, path), matcher);
        }
        return this;
    }

    public <T> JsonAsserter assertEquals(String path, T expected) {
        return assertThat(path, equalTo(expected));
    }

    public JsonAsserter assertNotDefined(String path) {
        if (!JsonPath.read(jsonObject, path).isEmpty()) {
            throw new AssertionError(
                    format("Document contains the path <%s> but was expected not to.", path));
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
