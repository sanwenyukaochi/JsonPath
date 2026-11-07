package com.jayway.jsonassert.impl.matcher;

import static org.hamcrest.core.IsEqual.equalTo;

import java.util.Map;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class IsMapContainingKey<K> extends MapTypeSafeMatcher<Map<K, ?>> {
    private final Matcher<K> keyMatcher;

    public IsMapContainingKey(Matcher<K> keyMatcher) {
        this.keyMatcher = keyMatcher;
    }

    @Override
    public boolean matchesSafely(Map<K, ?> item) {
        for (K key : item.keySet()) {
            if (keyMatcher.matches(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("map with key ").appendDescriptionOf(keyMatcher);
    }

    public static <K> Matcher<Map<K, ?>> hasKey(K key) {
        return hasKey(equalTo(key));
    }

    public static <K> Matcher<Map<K, ?>> hasKey(Matcher<K> keyMatcher) {
        return new IsMapContainingKey<K>(keyMatcher);
    }
}
