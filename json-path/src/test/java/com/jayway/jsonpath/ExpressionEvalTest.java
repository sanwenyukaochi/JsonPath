package com.jayway.jsonpath;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jayway.jsonpath.eval.ExpressionEvaluator;
import org.junit.jupiter.api.Test;

public class ExpressionEvalTest {

    @Test
    public void double_comparisons_preserve_the_fractional_part() {
        assertTrue(ExpressionEvaluator.eval(8.95D, ">", "8"));
        assertTrue(ExpressionEvaluator.eval(8.95D, ">=", "8.95"));
        assertTrue(ExpressionEvaluator.eval(8.95D, "<", "8.99"));
        assertTrue(ExpressionEvaluator.eval(8.95D, "<=", "8.95"));
        assertTrue(ExpressionEvaluator.eval(8.95D, "=", "8.95"));
        assertTrue(ExpressionEvaluator.eval(8.95D, "!=", "8.99"));
        assertTrue(ExpressionEvaluator.eval(8.95D, "<>", "8.99"));
        assertFalse(ExpressionEvaluator.eval(8.95D, "=", "8.99"));
        assertFalse(ExpressionEvaluator.eval(8.95D, ">", "8.99"));
        assertFalse(ExpressionEvaluator.eval(8.95D, "<=", "8"));
    }

    @Test
    public void quoted_string_comparisons_allow_whitespace_and_both_inequality_operators() {
        assertTrue(ExpressionEvaluator.eval("Nigel Rees", " = ", " 'Nigel Rees' "));
        assertFalse(ExpressionEvaluator.eval("Nigel Rees", "!=", "'Nigel Rees'"));
        assertTrue(ExpressionEvaluator.eval("Nigel Rees", "<>", "'Evelyn Waugh'"));
        assertTrue(ExpressionEvaluator.eval(2L, "<>", "1"));
        assertTrue(ExpressionEvaluator.eval(2, "<>", "1"));
    }

    @Test
    public void unsupported_types_and_operators_do_not_match() {
        assertFalse(ExpressionEvaluator.eval(null, "=", "null"));
        assertFalse(ExpressionEvaluator.eval(true, "=", "true"));
        assertFalse(ExpressionEvaluator.eval("A", ">", "'B'"));
        assertFalse(ExpressionEvaluator.eval(1, "?", "1"));
    }

    @Test
    public void long_eval() throws Exception {

        assertTrue(ExpressionEvaluator.eval(1L, "=", "1"));
        assertTrue(ExpressionEvaluator.eval(2L, "!=", "1"));
        assertTrue(ExpressionEvaluator.eval(2L, ">", "1"));
        assertTrue(ExpressionEvaluator.eval(2L, ">=", "1"));
        assertTrue(ExpressionEvaluator.eval(2L, ">=", "2"));
        assertTrue(ExpressionEvaluator.eval(1L, "<", "2"));
        assertTrue(ExpressionEvaluator.eval(2L, "<=", "2"));

        assertFalse(ExpressionEvaluator.eval(1L, ">", "2"));
        assertFalse(ExpressionEvaluator.eval(1L, ">=", "2"));
        assertFalse(ExpressionEvaluator.eval(2L, "<", "1"));
        assertFalse(ExpressionEvaluator.eval(2L, "<=", "1"));
        assertFalse(ExpressionEvaluator.eval(1L, "=", "2"));
        assertFalse(ExpressionEvaluator.eval(1L, "!=", "1"));
    }

    @Test
    public void integer_eval() throws Exception {

        assertTrue(ExpressionEvaluator.eval(1, "=", "1"));
        assertTrue(ExpressionEvaluator.eval(2, "!=", "1"));
        assertTrue(ExpressionEvaluator.eval(2, ">", "1"));
        assertTrue(ExpressionEvaluator.eval(2, ">=", "1"));
        assertTrue(ExpressionEvaluator.eval(2, ">=", "2"));
        assertTrue(ExpressionEvaluator.eval(1, "<", "2"));
        assertTrue(ExpressionEvaluator.eval(2, "<=", "2"));

        assertFalse(ExpressionEvaluator.eval(1, ">", "2"));
        assertFalse(ExpressionEvaluator.eval(1, ">=", "2"));
        assertFalse(ExpressionEvaluator.eval(2, "<", "1"));
        assertFalse(ExpressionEvaluator.eval(2, "<=", "1"));
        assertFalse(ExpressionEvaluator.eval(1, "=", "2"));
        assertFalse(ExpressionEvaluator.eval(1, "!=", "1"));
    }

    @Test
    public void double_eval() throws Exception {

        assertTrue(ExpressionEvaluator.eval(1D, "=", "1"));
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
        assertFalse(ExpressionEvaluator.eval(1D, "=", "2"));
        assertFalse(ExpressionEvaluator.eval(1D, "!=", "1"));
    }

    @Test
    public void string_eval() throws Exception {

        assertTrue(ExpressionEvaluator.eval("A", "=", "A"));
        assertTrue(ExpressionEvaluator.eval("B", "!=", "A"));
    }
}
