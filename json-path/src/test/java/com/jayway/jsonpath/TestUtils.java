package com.jayway.jsonpath;

import static com.jayway.jsonpath.JsonPath.using;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public final class TestUtils {
    private TestUtils() {}

    public static void assertEvaluationThrows(
            final String json, final String path, Class<? extends JsonPathException> expected) {
        assertEvaluationThrows(json, path, expected, Configuration.defaultConfiguration());
    }

    public static void assertEvaluationThrows(
            final String json,
            final String path,
            Class<? extends JsonPathException> expected,
            final Configuration conf) {
        try {
            using(conf).parse(json).read(path);
            fail("Should throw " + expected.getName());
        } catch (JsonPathException exc) {
            if (exc.getClass() != expected) throw exc;
        }
    }

    public static void assertHasNoResults(
            final String json, final String path, Configuration conf) {
        assertHasResults(json, path, 0, conf);
    }

    public static void assertHasOneResult(
            final String json, final String path, Configuration conf) {
        assertHasResults(json, path, 1, conf);
    }

    public static void assertHasResults(
            final String json,
            final String path,
            final int expectedResultCount,
            Configuration conf) {
        Object result = JsonPath.using(conf).parse(json).read(path);
        assertThat(conf.jsonProvider().length(result)).isEqualTo(expectedResultCount);
    }
}
