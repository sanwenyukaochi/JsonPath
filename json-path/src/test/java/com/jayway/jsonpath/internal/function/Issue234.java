package com.jayway.jsonpath.internal.function;

import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.JsonPath;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class Issue234 {

    @Test
    public void testIssue234() {
        Map<String, String> context = new HashMap<String, String>();
        context.put("key", "first");
        Object value = JsonPath.read(context, "concat(\"/\", $.key)");
        assertThat(value).isEqualTo("/first");
        context.put("key", "second");
        value = JsonPath.read(context, "concat(\"/\", $.key)");
        assertThat(value).isEqualTo("/second");
    }
}
