package com.jayway.jsonpath;

import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.gson.JsonArray;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ScientificNotationTest extends BaseTest {

    final String sci_rep_array =
"""
{"num_array": [{"num":1},{"num":-1e-10},{"num":0.1e10},{"num":2E-20},{"num":-0.2E20} ]}\
""";

    @Test
    public void testScientificNotation() {
        List<JsonArray> result =
                using(Configuration.defaultConfiguration())
                        .parse(sci_rep_array)
                        .read(
                                "$.num_array[?(@.num == 1 || @.num == -1e-10 || @.num == 0.1e10 || @.num == 2E-20 || @.num == -0.2E20)]");

        assertThat(result.toString())
                .isEqualTo(
                        "[{\"num\":1},{\"num\":-1.0E-10},{\"num\":1.0E9},{\"num\":2.0E-20},{\"num\":-2.0E19}]");
    }

    @Test
    public void testScientificNotation_lt_gt() {
        List<JsonArray> result;
        result =
                using(Configuration.defaultConfiguration())
                        .parse(sci_rep_array)
                        .read("$.num_array[?(@.num > -0.0E0)]");

        assertThat(result.toString()).isEqualTo("[{\"num\":1},{\"num\":1.0E9},{\"num\":2.0E-20}]");

        result =
                using(Configuration.defaultConfiguration())
                        .parse(sci_rep_array)
                        .read("$.num_array[?(@.num < -0.0E0)]");

        assertThat(result.toString()).isEqualTo("[{\"num\":-1.0E-10},{\"num\":-2.0E19}]");
    }
}
