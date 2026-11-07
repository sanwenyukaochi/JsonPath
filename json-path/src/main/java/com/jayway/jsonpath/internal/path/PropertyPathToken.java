package com.jayway.jsonpath.internal.path;

import static com.jayway.jsonpath.internal.Utils.onlyOneIsTrueNonThrow;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.Utils;
import java.util.ArrayList;
import java.util.List;

public class PropertyPathToken extends PathToken {

    private final List<String> properties;
    private final String stringDelimiter;

    public PropertyPathToken(List<String> properties, char stringDelimiter) {
        if (properties.isEmpty()) {
            throw new InvalidPathException("Empty properties");
        }
        this.properties = properties;
        this.stringDelimiter = Character.toString(stringDelimiter);
    }

    public List<String> getProperties() {
        return properties;
    }

    public boolean singlePropertyCase() {
        return properties.size() == 1;
    }

    public boolean multiPropertyMergeCase() {
        return isLeaf() && properties.size() > 1;
    }

    public boolean multiPropertyIterationCase() {

        return !isLeaf() && properties.size() > 1;
    }

    @Override
    public void evaluate(
            String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {

        assert onlyOneIsTrueNonThrow(
                singlePropertyCase(), multiPropertyMergeCase(), multiPropertyIterationCase());

        if (!ctx.jsonProvider().isMap(model)) {
            if (!isUpstreamDefinite() || ctx.options().contains(Option.SUPPRESS_EXCEPTIONS)) {
                return;
            } else {
                String m = model == null ? "null" : model.getClass().getName();
                throw new PathNotFoundException(
                        String.format(
                                "Expected to find an object with property %s in path %s but found '%s'. "
                                        + "This is not a json object according to the JsonProvider: '%s'.",
                                getPathFragment(),
                                currentPath,
                                m,
                                ctx.configuration().jsonProvider().getClass().getName()));
            }
        }

        if (singlePropertyCase() || multiPropertyMergeCase()) {
            handleObjectProperty(currentPath, model, ctx, properties);
            return;
        }

        assert multiPropertyIterationCase();
        final List<String> currentlyHandledProperty = new ArrayList<String>(1);
        currentlyHandledProperty.add(null);
        for (final String property : properties) {
            currentlyHandledProperty.set(0, property);
            handleObjectProperty(currentPath, model, ctx, currentlyHandledProperty);
        }
    }

    @Override
    public boolean isTokenDefinite() {

        return singlePropertyCase() || multiPropertyMergeCase();
    }

    @Override
    public String getPathFragment() {
        return new StringBuilder()
                .append("[")
                .append(Utils.join(",", stringDelimiter, properties))
                .append("]")
                .toString();
    }
}
