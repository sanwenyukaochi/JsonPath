package com.jayway.jsonpath.internal.function;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configurations;
import com.jayway.jsonpath.JsonPath;
import java.io.InputStream;
import org.junit.jupiter.api.Test;

public class Issue191 {

    private Configuration conf = Configurations.GSON_CONFIGURATION;

    @Test
    public void testResultSetNumericComputation() {
        InputStream stream = ClassLoader.getSystemResourceAsStream("issue_191.json");
        Long value = JsonPath.parse(stream).read("$.sum($..timestamp)", Long.class);
        assertEquals(
                Long.valueOf(35679716813L),
                value,
                "Expected the max function to consume the aggregation parameters and calculate the max over the result set");
    }

    @Test
    public void testResultSetNumericComputationTail() {
        InputStream stream = ClassLoader.getSystemResourceAsStream("issue_191.json");
        Long value = JsonPath.parse(stream).read("$..timestamp.sum()", Long.class);
        assertEquals(
                Long.valueOf(35679716813L),
                value,
                "Expected the max function to consume the aggregation parameters and calculate the max over the result set");
    }

    @Test
    public void testResultSetNumericComputationRecursiveReplacement() {
        InputStream stream = ClassLoader.getSystemResourceAsStream("issue_191.json");
        Long value =
                JsonPath.parse(stream)
                        .read("$.max($..timestamp.avg(), $..timestamp.stddev())", Long.class);
        assertEquals(
                Long.valueOf(1427188672L),
                value,
                "Expected the max function to consume the aggregation parameters and calculate the max over the result set");
    }

    @Test
    public void testMultipleResultSetSums() {
        InputStream stream = ClassLoader.getSystemResourceAsStream("issue_191.json");
        Long value = JsonPath.parse(stream).read("$.sum($..timestamp, $..cpus)", Long.class);
        assertEquals(
                Long.valueOf(35679716835L),
                value,
                "Expected the max function to consume the aggregation parameters and calculate the max over the result set");
    }

    @Test
    public void testConcatResultSet() {
        InputStream stream = ClassLoader.getSystemResourceAsStream("issue_191.json");
        String concatResult = JsonPath.parse(stream).read("$.concat($..state)", String.class);
        assertEquals(
                concatResult.length(),
                806,
                "Expected a string length to be a concat of all of the states");
    }

    @Test
    public void testConcatWithNumericValueAsString() {
        InputStream stream = ClassLoader.getSystemResourceAsStream("issue_191.json");
        String concatResult = JsonPath.parse(stream).read("$.concat($..cpus)", String.class);
        assertEquals(
                concatResult.length(),
                489,
                "Expected a string length to be a concat of all of the cpus");
    }
}
