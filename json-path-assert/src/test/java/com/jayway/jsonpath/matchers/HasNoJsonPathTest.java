package com.jayway.jsonpath.matchers;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasNoJsonPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.api.Test;

public class HasNoJsonPathTest {
    private static final String JSON_STRING = "{" + "\"name\": \"Jessie\"" + "}";

    @Test
    public void shouldMatchMissingJsonPath() {
        assertThat(JSON_STRING, hasNoJsonPath("$.not_there"));
    }

    @Test
    public void shouldNotMatchExistingJsonPath() {
        assertThat(JSON_STRING, not(hasNoJsonPath("$.name")));
    }

    @Test
    public void shouldBeDescriptive() {
        assertThat(
                hasNoJsonPath("$.name"),
                hasToString(equalTo("is json without json path \"$['name']\"")));
    }
}
