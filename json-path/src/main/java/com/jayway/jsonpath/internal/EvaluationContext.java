package com.jayway.jsonpath.internal;

import com.jayway.jsonpath.Configuration;

import java.util.List;

public interface EvaluationContext {

    Configuration configuration();

    <T> T getValue();

    <T> T getPath();

    List<String> getPathList();
}
