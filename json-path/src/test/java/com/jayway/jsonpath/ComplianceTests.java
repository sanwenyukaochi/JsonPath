package com.jayway.jsonpath;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

public class ComplianceTests {

    @Test
    public void test_one() throws Exception {

        String json =
                """
                {
                    a: "a",
                    b: "b",
                    "c d": "e"
                }\
                """;

        assertThat(JsonPath.<String>read(json, "$.a"), equalTo("a"));
        assertThat(JsonPath.<List<String>>read(json, "$.*"), hasItems("a", "b", "e"));
        assertThat(JsonPath.<List<String>>read(json, "$['*']"), hasItems("a", "b", "e"));
        assertThat(JsonPath.<String>read(json, "$['a']"), equalTo("a"));
    }

    @Test
    public void test_two() throws Exception {
        String json =
                """
                [ 1, "2", 3.14, true, null ]\
                """;

        assertThat(JsonPath.<Integer>read(json, "$[0]"), is(equalTo(1)));
        assertThat(JsonPath.<Integer>read(json, "$[4]"), is(equalTo(null)));
        assertThat(
                JsonPath.<List<Comparable>>read(json, "$[*]"),
                hasItems(
                        (Comparable) new Integer(1),
                        (Comparable) new String("2"),
                        (Comparable) new Double(3.14),
                        (Comparable) new Boolean(true),
                        null));
        assertThat(JsonPath.<Boolean>read(json, "$[-1:]"), is(equalTo(null)));
    }

    @Test
    public void test_three() throws Exception {
        String json =
                """
                {
                    points: [
                        { id: "i1", x: 4, y: -5 },
                        { id: "i2", x: -2, y: 2, z: 1 },
                        { id: "i3", x: 8, y: 3 },
                        { id: "i4", x: -6, y: -1 },
                        { id: "i5", x: 0, y: 2, z: 1 },
                        { id: "i6", x: 1, y: 4 }
                    ]
                }\
                """;

        assertThat(
                JsonPath.<Map<String, Comparable>>read(json, "$.points[1]"),
                allOf(
                        Matchers.<String, Comparable>hasEntry("id", "i2"),
                        Matchers.<String, Comparable>hasEntry("x", -2),
                        Matchers.<String, Comparable>hasEntry("y", 2),
                        Matchers.<String, Comparable>hasEntry("z", 1)));

        assertThat(JsonPath.<Integer>read(json, "$.points[4].x"), equalTo(0));

        assertThat(JsonPath.<List<Integer>>read(json, "$.points[?(@.id == 'i4')].x"), hasItem(-6));

        assertThat(
                JsonPath.<List<Integer>>read(json, "$.points[*].x"), hasItems(4, -2, 8, -6, 0, 1));

        assertThat(JsonPath.<List<String>>read(json, "$.points[?(@.z)].id"), hasItems("i2", "i5"));

        assertThat(JsonPath.<String>read(json, "$.points[(@.length-1)].id"), equalTo("i6"));
    }

    @Test
    public void test_four() throws Exception {
        String json =
                """
                {
                    "menu": {
                        "header": "SVG Viewer",
                        "items": [
                            {"id": "Open"},
                            {"id": "OpenNew", "label": "Open New"},
                            null,
                            {"id": "ZoomIn", "label": "Zoom In"},
                            {"id": "ZoomOut", "label": "Zoom Out"},
                            {"id": "OriginalView", "label": "Original View"},
                            null,
                            {"id": "Quality"},
                            {"id": "Pause"},
                            {"id": "Mute"},
                            null,
                            {"id": "Find", "label": "Find..."},
                            {"id": "FindAgain", "label": "Find Again"},
                            {"id": "Copy"},
                            {"id": "CopyAgain", "label": "Copy Again"},
                            {"id": "CopySVG", "label": "Copy SVG"},
                            {"id": "ViewSVG", "label": "View SVG"},
                            {"id": "ViewSource", "label": "View Source"},
                            {"id": "SaveAs", "label": "Save As"},
                            null,
                            {"id": "Help"},
                            {"id": "About", "label": "About Adobe CVG Viewer..."}
                        ]
                    }
                }\
                """;
    }
}
