package com.jayway.jsonpath.internal;

import com.jayway.jsonpath.Configuration;

import java.util.Collection;
import java.util.List;

public interface EvaluationContext {

    Configuration configuration();

    Object rootDocument();

    <T> T getValue();

    <T> T getPath();

    List<String> getPathList();

    Collection<PathRef> updateOperations();
}
