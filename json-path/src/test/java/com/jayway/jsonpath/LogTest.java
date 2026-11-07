package com.jayway.jsonpath;

import com.jayway.jsonpath.internal.Log;

import org.junit.jupiter.api.Test;

public class LogTest {

    @Test
    public void logger_expands_templates() {

        Log.enableDebug();

        Log.debug("foo \n{}", "bar");
    }
}
