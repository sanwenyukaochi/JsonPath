package com.jayway.jsonpath.internal.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jayway.jsonpath.BaseTest;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.MethodSource;

@ParameterizedClass
@MethodSource("data")
public class PatternFlagTest extends BaseTest {

    private final int flags;
    private final String expectedFlags;

    public PatternFlagTest(int flags, String expectedFlags) {
        this.flags = flags;
        this.expectedFlags = expectedFlags;
    }

    @Test
    public void testParseFlags() {
        assertEquals(expectedFlags, PatternFlag.parseFlags(flags));
    }

    public static Iterable data() {
        return Arrays.asList(
                new Object[][] {
                    {1, "d"},
                    {2, "i"},
                    {4, "x"},
                    {8, "m"},
                    {32, "s"},
                    {64, "u"},
                    {256, "U"},
                    {300, "xmsU"},
                    {13, "dxm"},
                    {7, "dix"},
                    {100, "xsu"},
                    {367, "dixmsuU"}
                });
    }
}
