package com.jayway.jsonpath.old;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jayway.jsonpath.JsonPath;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

public class ArraySlicingTest {

    public static final String JSON_ARRAY = "[1, 3, 5, 7, 8, 13, 20]";

    @Test
    public void get_by_position() {
        Integer result = JsonPath.read(JSON_ARRAY, "$[3]");
        assertEquals(7, result.intValue());
    }

    @Test
    public void get_from_index() {
        List<Integer> result = JsonPath.read(JSON_ARRAY, "$[:3]");
        assertThat(result, Matchers.contains(1, 3, 5));
    }

    @Test
    public void get_between_index() {
        List<Integer> result = JsonPath.read(JSON_ARRAY, "$[1:5]");
        assertThat(result, Matchers.contains(3, 5, 7, 8));
    }

    @Test
    public void get_between_index_2() {
        List<Integer> result = JsonPath.read(JSON_ARRAY, "$[0:1]");
        assertThat(result, Matchers.contains(1));
    }

    @Test
    public void get_between_index_out_of_bounds() {
        List<Integer> result = JsonPath.read(JSON_ARRAY, "$[1:15]");
        assertThat(result, Matchers.contains(3, 5, 7, 8, 13, 20));
    }

    @Test
    public void get_from_tail_index() {
        List<Integer> result = JsonPath.read(JSON_ARRAY, "$[-3:]");
        assertThat(result, Matchers.contains(8, 13, 20));
    }

    @Test
    public void get_from_tail() {
        List<Integer> result = JsonPath.read(JSON_ARRAY, "$[3:]");
        assertThat(result, Matchers.contains(7, 8, 13, 20));
    }

    @Test
    public void get_indexes() {
        List<Integer> result = JsonPath.read(JSON_ARRAY, "$[0,1,2]");
        assertThat(result, Matchers.contains(1, 3, 5));
    }

    @Test
    public void get_between_index_3() {
        List<Integer> result = JsonPath.read(JSON_ARRAY, "$[0:2]");
        assertThat(result, Matchers.contains(1, 3));
    }
}
