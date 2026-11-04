package com.jayway.jsonpath;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jayway.jsonpath.eval.Expression;
import org.junit.jupiter.api.Test;

public class ExpressionEvalTest {

    @Test
    public void double_comparisons_preserve_the_fractional_part() {
        assertTrue(Expression.eval(8.95D, ">", "8"));
        assertTrue(Expression.eval(8.95D, ">=", "8.95"));
        assertTrue(Expression.eval(8.95D, "<", "8.99"));
        assertTrue(Expression.eval(8.95D, "<=", "8.95"));
        assertTrue(Expression.eval(8.95D, "=", "8.95"));
        assertTrue(Expression.eval(8.95D, "!=", "8.99"));
        assertTrue(Expression.eval(8.95D, "<>", "8.99"));
        assertFalse(Expression.eval(8.95D, "=", "8.99"));
        assertFalse(Expression.eval(8.95D, ">", "8.99"));
        assertFalse(Expression.eval(8.95D, "<=", "8"));
    }

    @Test
    public void quoted_string_comparisons_allow_whitespace_and_both_inequality_operators() {
        assertTrue(Expression.eval("Nigel Rees", " = ", " 'Nigel Rees' "));
        assertFalse(Expression.eval("Nigel Rees", "!=", "'Nigel Rees'"));
        assertTrue(Expression.eval("Nigel Rees", "<>", "'Evelyn Waugh'"));
        assertTrue(Expression.eval(2L, "<>", "1"));
        assertTrue(Expression.eval(2, "<>", "1"));
    }

    @Test
    public void unsupported_types_and_operators_do_not_match() {
        assertFalse(Expression.eval(null, "=", "null"));
        assertFalse(Expression.eval(true, "=", "true"));
        assertFalse(Expression.eval("A", ">", "'B'"));
        assertFalse(Expression.eval(1, "?", "1"));
    }

    @Test
    public void long_eval() throws Exception {

        assertTrue(Expression.eval(1L, "=", "1"));
        assertTrue(Expression.eval(2L, "!=", "1"));
        assertTrue(Expression.eval(2L, ">", "1"));
        assertTrue(Expression.eval(2L, ">=", "1"));
        assertTrue(Expression.eval(2L, ">=", "2"));
        assertTrue(Expression.eval(1L, "<", "2"));
        assertTrue(Expression.eval(2L, "<=", "2"));

        assertFalse(Expression.eval(1L, ">", "2"));
        assertFalse(Expression.eval(1L, ">=", "2"));
        assertFalse(Expression.eval(2L, "<", "1"));
        assertFalse(Expression.eval(2L, "<=", "1"));
        assertFalse(Expression.eval(1L, "=", "2"));
        assertFalse(Expression.eval(1L, "!=", "1"));
    }

    @Test
    public void integer_eval() throws Exception {

        assertTrue(Expression.eval(1, "=", "1"));
        assertTrue(Expression.eval(2, "!=", "1"));
        assertTrue(Expression.eval(2, ">", "1"));
        assertTrue(Expression.eval(2, ">=", "1"));
        assertTrue(Expression.eval(2, ">=", "2"));
        assertTrue(Expression.eval(1, "<", "2"));
        assertTrue(Expression.eval(2, "<=", "2"));

        assertFalse(Expression.eval(1, ">", "2"));
        assertFalse(Expression.eval(1, ">=", "2"));
        assertFalse(Expression.eval(2, "<", "1"));
        assertFalse(Expression.eval(2, "<=", "1"));
        assertFalse(Expression.eval(1, "=", "2"));
        assertFalse(Expression.eval(1, "!=", "1"));
    }

    @Test
    public void double_eval() throws Exception {

        assertTrue(Expression.eval(1D, "=", "1"));
        assertTrue(Expression.eval(2D, "!=", "1"));
        assertTrue(Expression.eval(2D, ">", "1"));
        assertTrue(Expression.eval(2D, ">=", "1"));
        assertTrue(Expression.eval(2D, ">=", "2"));
        assertTrue(Expression.eval(1D, "<", "2"));
        assertTrue(Expression.eval(2D, "<=", "2"));

        assertFalse(Expression.eval(1D, ">", "2"));
        assertFalse(Expression.eval(1D, ">=", "2"));
        assertFalse(Expression.eval(2D, "<", "1"));
        assertFalse(Expression.eval(2D, "<=", "1"));
        assertFalse(Expression.eval(1D, "=", "2"));
        assertFalse(Expression.eval(1D, "!=", "1"));
    }

    @Test
    public void string_eval() throws Exception {

        assertTrue(Expression.eval("A", "=", "A"));
        assertTrue(Expression.eval("B", "!=", "A"));
    }
}
