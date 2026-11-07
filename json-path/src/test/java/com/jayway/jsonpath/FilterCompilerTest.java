package com.jayway.jsonpath;

import com.jayway.jsonpath.internal.filter.FilterCompiler;

import org.junit.jupiter.api.Test;

public class FilterCompilerTest {

    @Test
    public void filter_compiler_test() {

        FilterCompiler.compile("[?(@.message == 'it\\\\')]");
    }
}
