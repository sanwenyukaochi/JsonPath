package com.jayway.jsonpath.internal.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RelationalOperatorTest {

    Locale locale;

    @BeforeEach
    public void saveDefaultLocale() {
        locale = Locale.getDefault();
    }

    @AfterEach
    public void restoreDefaultLocale() {
        Locale.setDefault(locale);
    }

    @Test
    public void testFromStringWithEnglishLocale() {
        Locale.setDefault(Locale.ENGLISH);
        assertEquals(RelationalOperator.IN, RelationalOperator.fromString("in"));
        assertEquals(RelationalOperator.IN, RelationalOperator.fromString("IN"));
    }

    @Test
    public void testFromStringWithTurkishLocale() {
        Locale.setDefault(new Locale("tr", "TR"));
        assertEquals(RelationalOperator.IN, RelationalOperator.fromString("in"));
        assertEquals(RelationalOperator.IN, RelationalOperator.fromString("IN"));
    }
}
