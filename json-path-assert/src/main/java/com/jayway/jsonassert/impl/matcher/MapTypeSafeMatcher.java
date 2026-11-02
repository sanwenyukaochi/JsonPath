package com.jayway.jsonassert.impl.matcher;

import java.util.Map;
import org.hamcrest.BaseMatcher;

public abstract class MapTypeSafeMatcher<M extends Map<?, ?>> extends BaseMatcher<M> {
    @SuppressWarnings("unchecked")
    public boolean matches(Object item) {
        return item instanceof Map && matchesSafely((M) item);
    }

    protected abstract boolean matchesSafely(M map);
}
