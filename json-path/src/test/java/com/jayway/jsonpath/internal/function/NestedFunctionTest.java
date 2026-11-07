package com.jayway.jsonpath.internal.function;

import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configurations;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParameterizedClass
@MethodSource("configurations")
public class NestedFunctionTest extends BaseFunctionTest {
    private static HttpServer httpFixtureServer;
    private static String httpFixtureJson;

    @BeforeAll
    public static void startHttpFixture() throws IOException {
        httpFixtureServer = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        httpFixtureServer.createContext(
                "/countries/all/",
                exchange -> {
                    byte[] response = "[{\"total\":264}]".getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders()
                            .set("Content-Type", "application/json; charset=UTF-8");
                    exchange.sendResponseHeaders(200, response.length);
                    try (OutputStream output = exchange.getResponseBody()) {
                        output.write(response);
                    }
                });
        httpFixtureServer.start();
        httpFixtureJson =
                "{\"urls\":[\"http://127.0.0.1:"
                        + httpFixtureServer.getAddress().getPort()
                        + "/countries/all/?format=json\"]}";
    }

    @AfterAll
    public static void stopHttpFixture() {
        if (httpFixtureServer != null) httpFixtureServer.stop(0);
    }

    private static final Logger logger = LoggerFactory.getLogger(NumericPathFunctionTest.class);

    private Configuration conf = Configurations.GSON_CONFIGURATION;

    public NestedFunctionTest(Configuration conf) {
        logger.debug("Testing with configuration {}", conf.getClass().getName());
        this.conf = conf;
    }

    public static Iterable<Configuration> configurations() {
        return Configurations.configurations();
    }

    @Test
    public void testParameterAverageFunctionCall() {
        verifyMathFunction(conf, "$.avg($.numbers.min(), $.numbers.max())", 5.5);
    }

    @Test
    public void testArrayAverageFunctionCall() {
        verifyMathFunction(conf, "$.numbers.avg()", 5.5);
    }

    @Test
    public void testArrayAverageFunctionCallWithParameters() {
        verifyMathFunction(conf, "$.numbers.sum($.numbers.min(), $.numbers.max())", 66.0);
    }

    @Test
    public void testJsonInnerArgumentArray() {
        verifyMathFunction(conf, "$.sum(5, 3, $.numbers.max(), 2)", 20.0);
    }

    @Test
    public void testStringConcat() {
        verifyTextFunction(conf, "$.text.concat()", "abcdef");
    }

    @Test
    public void testStringConcatWithJSONParameter() {
        verifyTextFunction(conf, "$.text.concat(\"-\", \"ghijk\")", "abcdef-ghijk");
    }

    @Test
    public void testLoadFunction() {
        Object result = using(conf).parse(httpFixtureJson).read("$.getjson($.urls[0])[0].total");
        Object value = conf.jsonProvider().unwrap(result);
        assertTrue(value instanceof Number);
        assertThat(new BigDecimal(value.toString())).isEqualByComparingTo("264");
    }

    @Test
    public void testAppendNumber() {
        verifyMathFunction(
                conf, "$.numbers.append(11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 0).avg()", 10.0);
    }

    @Test
    public void testAppendTextAndNumberThenSum() {
        verifyMathFunction(conf, "$.numbers.append(\"0\", \"11\").sum()", 55.0);
    }

    @Test
    public void testErrantCloseBraceNegative() {
        try {
            using(conf).parse(this.NUMBER_SERIES).read("$.numbers.append(0, 1, 2}).avg()");
            assert (false);
        } catch (Exception e) {
            assertTrue(e.getMessage().startsWith("Unexpected close brace"));
        }
    }

    @Test
    public void testErrantCloseBracketNegative() {
        try {
            using(conf).parse(this.NUMBER_SERIES).read("$.numbers.append(0, 1, 2]).avg()");
            assert (false);
        } catch (Exception e) {
            assertTrue(e.getMessage().startsWith("Unexpected close bracket"));
        }
    }

    @Test
    public void testUnclosedFunctionCallNegative() {
        try {
            using(conf).parse(this.NUMBER_SERIES).read("$.numbers.append(0, 1, 2");
            assert (false);
        } catch (Exception e) {
            assertTrue(e.getMessage().startsWith("Arguments to function: 'append'"));
        }
    }
}
