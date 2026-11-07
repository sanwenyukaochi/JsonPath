package com.jayway.jsonpath;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Issue_487 {

    public static final Configuration jsonConf = Configuration.defaultConfiguration();

    @Test
    public void test_read_with_comma_1() {
        DocumentContext dc = JsonPath.using(jsonConf).parse("{ \"key,\" : \"value\" }");
        Object ans = dc.read(JsonPath.compile("$['key,']"));

        Assertions.assertTrue(ans.toString().equals("value"));
    }

    @Test
    public void test_read_with_comma_2() {
        DocumentContext dc = JsonPath.using(jsonConf).parse("{ \"key,\" : \"value\" }");
        Object ans = dc.read(JsonPath.compile("$['key\\,']"));

        Assertions.assertTrue(ans.toString().equals("value"));
    }
}
