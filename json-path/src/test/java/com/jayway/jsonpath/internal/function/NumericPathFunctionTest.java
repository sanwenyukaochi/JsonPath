package com.jayway.jsonpath.internal.function;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configurations;
import com.jayway.jsonpath.JsonPathException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParameterizedClass
@MethodSource("configurations")
public class NumericPathFunctionTest extends BaseFunctionTest {

    private static final Logger logger = LoggerFactory.getLogger(NumericPathFunctionTest.class);

    private Configuration conf = Configurations.GSON_CONFIGURATION;

    public NumericPathFunctionTest(Configuration conf) {
        logger.debug("Testing with configuration {}", conf.getClass().getName());
        this.conf = conf;
    }

    public static Iterable<Configuration> configurations() {
        return Configurations.configurations();
    }

    @Test
    public void testAverageOfDoubles() {
        verifyMathFunction(conf, "$.numbers.avg()", 5.5);
    }

    @Test
    public void testSumOfDouble() {
        verifyMathFunction(conf, "$.numbers.sum()", (10d * (10d + 1d)) / 2d);
    }

    @Test
    public void testMaxOfDouble() {
        verifyMathFunction(conf, "$.numbers.max()", 10d);
    }

    @Test
    public void testMinOfDouble() {
        verifyMathFunction(conf, "$.numbers.min()", 1d);
    }

    @Test
    public void testStdDevOfDouble() {
        verifyMathFunction(conf, "$.numbers.stddev()", 2.8722813232690143d);
    }

    @Test
    public void testAverageOfEmptyListNegative() {
        try {
            verifyMathFunction(conf, "$.empty.avg()", null);
        } catch (JsonPathException e) {
            assertEquals(
                    e.getMessage(),
                    "Aggregation function attempted to calculate value using empty array");
        }
    }

    @Test
    public void testMaxOfEmptyListNegative() {
        try {
            verifyMathFunction(conf, "$.empty.max()", null);
        } catch (JsonPathException e) {
            assertEquals(
                    e.getMessage(),
                    "Aggregation function attempted to calculate value using empty array");
        }
    }

    @Test
    public void testMinOfEmptyListNegative() {
        try {
            verifyMathFunction(conf, "$.empty.min()", null);
        } catch (JsonPathException e) {
            assertEquals(
                    e.getMessage(),
                    "Aggregation function attempted to calculate value using empty array");
        }
    }

    @Test
    public void testStddevOfEmptyListNegative() {
        try {
            verifyMathFunction(conf, "$.empty.stddev()", null);
        } catch (JsonPathException e) {
            assertEquals(
                    e.getMessage(),
                    "Aggregation function attempted to calculate value using empty array");
        }
    }

    @Test
    public void testSumOfEmptyListNegative() {
        try {
            verifyMathFunction(conf, "$.empty.sum()", null);
        } catch (JsonPathException e) {
            assertEquals(
                    e.getMessage(),
                    "Aggregation function attempted to calculate value using empty array");
        }
    }
}
