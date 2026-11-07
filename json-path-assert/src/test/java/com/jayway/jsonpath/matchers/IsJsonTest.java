package com.jayway.jsonpath.matchers;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.jayway.jsonpath.matchers.helpers.ResourceHelpers.resource;
import static com.jayway.jsonpath.matchers.helpers.ResourceHelpers.resourceAsFile;
import static com.jayway.jsonpath.matchers.helpers.TestingMatchers.withPathEvaluatedTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.matchers.helpers.StrictParsingConfiguration;
import com.jayway.jsonpath.matchers.helpers.TestingMatchers;
import java.io.File;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class IsJsonTest {
    private static final String BOOKS_JSON_STRING = resource("books.json");
    private static final File BOOKS_JSON_FILE = resourceAsFile("books.json");

    @BeforeAll
    public static void setupStrictJsonParsing() {
        Configuration.setDefaults(new StrictParsingConfiguration());
    }

    @AfterAll
    public static void setupDefaultJsonParsing() {
        Configuration.setDefaults(null);
    }

    @Test
    public void shouldMatchJsonObjectEvaluatedToTrue() {
        Object parsedJson = parseJson(BOOKS_JSON_STRING);
        assertThat(parsedJson, isJson(withPathEvaluatedTo(true)));
    }

    @Test
    public void shouldNotMatchJsonObjectEvaluatedToFalse() {
        Object parsedJson = parseJson(BOOKS_JSON_STRING);
        assertThat(parsedJson, not(isJson(withPathEvaluatedTo(false))));
    }

    @Test
    public void shouldMatchJsonStringEvaluatedToTrue() {
        assertThat(BOOKS_JSON_STRING, isJson(withPathEvaluatedTo(true)));
    }

    @Test
    public void shouldNotMatchJsonStringEvaluatedToFalse() {
        assertThat(BOOKS_JSON_STRING, not(isJson(withPathEvaluatedTo(false))));
    }

    @Test
    public void shouldMatchJsonFileEvaluatedToTrue() {
        assertThat(BOOKS_JSON_FILE, isJson(withPathEvaluatedTo(true)));
    }

    @Test
    public void shouldNotMatchJsonFileEvaluatedToFalse() {
        assertThat(BOOKS_JSON_FILE, not(isJson(withPathEvaluatedTo(false))));
    }

    @Test
    public void shouldBeDescriptive() {
        Matcher<Object> matcher = isJson(withPathEvaluatedTo(true));
        Description description = new StringDescription();
        matcher.describeTo(description);
        assertThat(description.toString(), startsWith("is json"));
        assertThat(description.toString(), containsString(TestingMatchers.MATCH_TRUE_TEXT));
    }

    @Test
    public void shouldDescribeMismatchOfValidJson() {
        Matcher<Object> matcher = isJson(withPathEvaluatedTo(true));
        Description description = new StringDescription();
        matcher.describeMismatch(BOOKS_JSON_STRING, description);
        assertThat(description.toString(), containsString(TestingMatchers.MISMATCHED_TEXT));
    }

    @Test
    public void shouldDescribeMismatchOfInvalidJson() {
        Matcher<Object> matcher = isJson(withPathEvaluatedTo(true));
        Description description = new StringDescription();
        matcher.describeMismatch("invalid-json", description);
        assertThat(description.toString(), containsString("\"invalid-json\""));
    }

    private static Object parseJson(String json) {
        return Configuration.defaultConfiguration().jsonProvider().parse(json);
    }
}
