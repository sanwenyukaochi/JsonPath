package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configurations;
import java.util.Arrays;
import java.util.HashSet;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

public class KeySetFunctionTest extends BaseFunctionTest {

    private Configuration conf = Configurations.JACKSON_CONFIGURATION;

    @Test
    public void testKeySet() throws Exception {
        String json = IOUtils.toString(getClass().getResourceAsStream("/keyset.json"));
        verifyFunction(conf, "$.data.keys()", json, new HashSet<String>(Arrays.asList("a", "b")));
    }
}
