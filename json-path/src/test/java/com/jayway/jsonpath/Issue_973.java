package com.jayway.jsonpath;

import static org.assertj.core.api.Assertions.assertThatNoException;

import org.junit.jupiter.api.Test;

public class Issue_973 {
    @Test
    public void shouldNotCauseStackOverflow() {
        assertThatNoException().isThrownBy(() -> Criteria.parse("@[\"\",/\\"));
    }
}
