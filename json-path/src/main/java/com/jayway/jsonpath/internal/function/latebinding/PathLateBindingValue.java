package com.jayway.jsonpath.internal.function.latebinding;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.internal.Path;

import java.util.Objects;

public class PathLateBindingValue implements ILateBindingValue {
    private final Path path;
    private final String rootDocument;
    private final Configuration configuration;
    private final Object result;

    public PathLateBindingValue(
            final Path path, final Object rootDocument, final Configuration configuration) {
        this.path = path;
        this.rootDocument = rootDocument.toString();
        this.configuration = configuration;
        this.result = path.evaluate(rootDocument, rootDocument, configuration).getValue();
    }

    public Object get() {
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathLateBindingValue that = (PathLateBindingValue) o;
        return Objects.equals(path, that.path)
                && rootDocument.equals(that.rootDocument)
                && Objects.equals(configuration, that.configuration);
    }
}
