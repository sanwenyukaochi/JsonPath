package com.jayway.jsonpath.internal.function.latebinding;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.internal.Path;

public class PathLateBindingValue implements ILateBindingValue {
    private final Path path;
    private final Object rootDocument;
    private final Configuration configuration;

    public PathLateBindingValue(
            final Path path, final Object rootDocument, final Configuration configuration) {
        this.path = path;
        this.rootDocument = rootDocument;
        this.configuration = configuration;
    }

    public Object get() {
        return path.evaluate(rootDocument, rootDocument, configuration).getValue();
    }
}
