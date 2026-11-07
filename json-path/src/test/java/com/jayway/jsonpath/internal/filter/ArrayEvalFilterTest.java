package com.jayway.jsonpath.internal.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ArrayEvalFilterTest {

    @Test
    public void condition_statements_can_be_parsed() {

        assertEquals(
                new ArrayEvalFilter.ConditionStatement("@", "==", "5"),
                ArrayEvalFilter.createConditionStatement("[?(@==5)]"));
        assertEquals(
                new ArrayEvalFilter.ConditionStatement("@", "==", "5"),
                ArrayEvalFilter.createConditionStatement("[?(@ == 5)]"));
        assertEquals(
                new ArrayEvalFilter.ConditionStatement("@", "==", "5"),
                ArrayEvalFilter.createConditionStatement("[ ?(@ == 5) ]"));
        assertEquals(
                new ArrayEvalFilter.ConditionStatement("@", "==", "5"),
                ArrayEvalFilter.createConditionStatement("[ ?( @ == 5) ]"));
        assertEquals(
                new ArrayEvalFilter.ConditionStatement("@", "==", "5"),
                ArrayEvalFilter.createConditionStatement("[ ?( @ == 5 ) ]"));

        assertEquals(
                new ArrayEvalFilter.ConditionStatement("@", "==", "one"),
                ArrayEvalFilter.createConditionStatement("[?(@=='one')]"));
        assertEquals(
                new ArrayEvalFilter.ConditionStatement("@", "==", "one monkey"),
                ArrayEvalFilter.createConditionStatement("[?(@ == 'one monkey')]"));
        assertEquals(
                new ArrayEvalFilter.ConditionStatement("@", "==", "two"),
                ArrayEvalFilter.createConditionStatement("[?(@  == 'two')]"));

        assertEquals(
                new ArrayEvalFilter.ConditionStatement("@.name", "==", "true"),
                ArrayEvalFilter.createConditionStatement("[?(@.name == true)]"));

        assertEquals(
                new ArrayEvalFilter.ConditionStatement("@['name']", "==", "true"),
                ArrayEvalFilter.createConditionStatement("[?(@['name'] == true)]"));
        assertEquals(
                new ArrayEvalFilter.ConditionStatement("@.['name']", "==", "true"),
                ArrayEvalFilter.createConditionStatement("[?(@.['name'] == true)]"));

        assertEquals(
                new ArrayEvalFilter.ConditionStatement("@['name']['age']", "!=", "true"),
                ArrayEvalFilter.createConditionStatement("[?(@['name']['age'] != true)]"));
        assertEquals(
                new ArrayEvalFilter.ConditionStatement("@.['name'].age", ">", "true"),
                ArrayEvalFilter.createConditionStatement("[?(@.['name'].age > true)]"));
    }
}
