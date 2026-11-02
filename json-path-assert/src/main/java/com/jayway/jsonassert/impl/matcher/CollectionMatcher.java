package com.jayway.jsonassert.impl.matcher;

import java.util.Collection;
import org.hamcrest.BaseMatcher;

public abstract class CollectionMatcher<C extends Collection<?>> extends BaseMatcher<C> {
    @SuppressWarnings("unchecked")
    public boolean matches(Object item) {
        if (!(item instanceof Collection)) {
            return false;
        }
        return matchesSafely((C) item);
    }

    protected abstract boolean matchesSafely(C collection);
}
