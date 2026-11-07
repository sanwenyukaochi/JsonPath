package com.jayway.jsonpath.functions;

import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.Configuration;
import java.io.IOException;
import java.util.Scanner;

public class BaseFunctionTest {
    protected static final String NUMBER_SERIES =
            "{\"numbers\" : [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]}";
    protected static final String TEXT_SERIES =
            "{\"text\" : [ \"a\", \"b\", \"c\", \"d\", \"e\", \"f\" ]}";

    protected void verifyFunction(
            Configuration conf, String pathExpr, String json, Object expectedValue) {
        Object result = using(conf).parse(json).read(pathExpr);
        assertThat(result).isEqualTo(expectedValue);
    }

    protected void verifyMathFunction(Configuration conf, String pathExpr, Object expectedValue) {
        verifyFunction(conf, pathExpr, NUMBER_SERIES, expectedValue);
    }

    protected String getResourceAsText(String resourceName) throws IOException {
        return new Scanner(BaseFunctionTest.class.getResourceAsStream(resourceName), "UTF-8")
                .useDelimiter("\\A")
                .next();
    }
}
