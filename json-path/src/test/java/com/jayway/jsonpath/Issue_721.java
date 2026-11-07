package com.jayway.jsonpath;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Issue_721 {

    public static final Configuration jsonConf =
            Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS);

    @Test
    public void test_delete_1() {
        DocumentContext dc =
                JsonPath.using(jsonConf)
                        .parse("{\"top\": {\"middle\": null}}")
                        .delete(JsonPath.compile("$.top.middle.bottom"));
        Object ans = dc.read("$");

        Assertions.assertTrue(ans.toString().equals("{top={middle=null}}"));
    }

    @Test
    public void test_delete_2() {
        DocumentContext dc =
                JsonPath.using(jsonConf)
                        .parse(
"""
[{"top": {"middle": null}},{"top": {"middle": {}  }},{"top": {"middle": {bottom: 2}  }},]\
""")
                        .delete(JsonPath.compile("$[*].top.middle.bottom"));
        Object ans = dc.read("$");

        Assertions.assertTrue(
                ans.toString()
                        .equals(
                                "[{\"top\":{\"middle\":null}},{\"top\":{\"middle\":{}}},{\"top\":{\"middle\":{}}}]"));
    }
}
