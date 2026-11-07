package com.jayway.jsonpath;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Issue_537 {

    public static final Configuration jsonConf =
            Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS);

    @Test
    public void test_read() {
        Object ans = JsonPath.using(jsonConf).parse("{}").read("missing");
        Assertions.assertTrue(ans == null);
    }

    @Test
    public void test_renameKey() {
        List<Object> ans =
                JsonPath.using(jsonConf)
                        .parse(
"""
{"list":[{"data":{"old":1}},{"data":{}},{"data":{"old":2}}]}\
""")
                        .renameKey("$..data", "old", "new")
                        .read("$.list");
        Assertions.assertTrue(
                ans.toString()
                        .equals("[{\"data\":{\"new\":1}},{\"data\":{}},{\"data\":{\"new\":2}}]"));
    }
}
