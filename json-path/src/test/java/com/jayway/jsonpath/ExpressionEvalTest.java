package com.jayway.jsonpath;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jayway.jsonpath.internal.filter.eval.ExpressionEvaluator;
import org.junit.jupiter.api.Test;

public class ExpressionEvalTest {

    @Test
    public void long_eval() throws Exception {

        assertTrue(ExpressionEvaluator.eval(1L, "==", "1"));
        assertTrue(ExpressionEvaluator.eval(2L, "!=", "1"));
        assertTrue(ExpressionEvaluator.eval(2L, ">", "1"));
        assertTrue(ExpressionEvaluator.eval(2L, ">=", "1"));
        assertTrue(ExpressionEvaluator.eval(2L, ">=", "2"));
        assertTrue(ExpressionEvaluator.eval(1L, "<", "2"));
        assertTrue(ExpressionEvaluator.eval(2L, "<=", "2"));

        assertFalse(ExpressionEvaluator.eval(1, ">", "2"));
        assertFalse(ExpressionEvaluator.eval(1, ">=", "2"));
        assertFalse(ExpressionEvaluator.eval(2, "<", "1"));
        assertFalse(ExpressionEvaluator.eval(2, "<=", "1"));
        assertFalse(ExpressionEvaluator.eval(1, "==", "2"));
        assertFalse(ExpressionEvaluator.eval(1, "!=", "1"));
    }

    @Test
    public void double_eval() throws Exception {

        assertTrue(ExpressionEvaluator.eval(1D, "==", "1"));
        assertTrue(ExpressionEvaluator.eval(2D, "!=", "1"));
        assertTrue(ExpressionEvaluator.eval(2D, ">", "1"));
        assertTrue(ExpressionEvaluator.eval(2D, ">=", "1"));
        assertTrue(ExpressionEvaluator.eval(2D, ">=", "2"));
        assertTrue(ExpressionEvaluator.eval(1D, "<", "2"));
        assertTrue(ExpressionEvaluator.eval(2D, "<=", "2"));

        assertFalse(ExpressionEvaluator.eval(1D, ">", "2"));
        assertFalse(ExpressionEvaluator.eval(1D, ">=", "2"));
        assertFalse(ExpressionEvaluator.eval(2D, "<", "1"));
        assertFalse(ExpressionEvaluator.eval(2D, "<=", "1"));
        assertFalse(ExpressionEvaluator.eval(1D, "==", "2"));
        assertFalse(ExpressionEvaluator.eval(1D, "!=", "1"));
    }

    @Test
    public void string_eval() throws Exception {

        assertTrue(ExpressionEvaluator.eval("A", "==", "A"));
        assertTrue(ExpressionEvaluator.eval("B", "!=", "A"));
    }
}
