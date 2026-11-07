package com.jayway.jsonpath.spi.compiler;

import com.jayway.jsonpath.Configuration;

import java.util.List;

public interface EvaluationContext {

    Configuration configuration();

    <T> T getValue();

    <T> T getPath();

    List<String> getPathList();
}
