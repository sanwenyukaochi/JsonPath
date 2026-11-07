package com.jayway.jsonpath.internal;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JsonProviderFactory;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class FilterPathTokenTest {

    private static final Object ARRAY =
            JsonProviderFactory.createProvider()
                    .parse(
"""
[{
   "foo" : "foo-val-0"
},{
   "foo" : "foo-val-1"
},{
   "foo" : "foo-val-2"
},{
   "foo" : "foo-val-3"
},{
   "foo" : "foo-val-4"
},{
   "foo" : "foo-val-5"
},{
   "foo" : "foo-val-6"
}]\
""");

    private static Object ARRAY2 =
            JsonProviderFactory.createProvider()
                    .parse(
"""
[{
   "foo" : "foo-val-0",
   "int" : 0
,   "decimal" : 0.0
},{
   "foo" : "foo-val-1",
   "int" : 1,
   "decimal" : 0.1
},{
   "foo" : "foo-val-2",
   "int" : 2,
   "decimal" : 0.2
},{
   "foo" : "foo-val-3",
   "int" : 3,
   "decimal" : 0.3
},{
   "foo" : "foo-val-4",
   "int" : 4,
   "decimal" : 0.4
},{
   "foo" : "foo-val-5",
   "int" : 5,
   "decimal" : 0.5
},{
   "foo" : "foo-val-6",
   "int" : 6,
   "decimal" : 0.6
},{
   "foo" : "foo-val-7",
   "int" : 7,
   "decimal" : 0.7,
   "bool" : true
}]\
""");

    @Test
    public void a_filter_predicate_can_be_evaluated_on_string_criteria() {

        List<Map> result = JsonPath.read(ARRAY, "$[?(@.foo == 'foo-val-1')]");

        assertThat(result).containsOnly(singletonMap("foo", "foo-val-1"));
    }

    @Test
    public void a_filter_predicate_can_be_evaluated_on_int_criteria() {

        List<Map> result = JsonPath.read(ARRAY2, "$[?(@.int == 1)]");

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).contains(entry("int", 1));
    }

    @Test
    public void a_filter_predicate_can_be_evaluated_on_decimal_criteria() {

        List<Map> result = JsonPath.read(ARRAY2, "$[?(@.decimal == 0.1)]");

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).contains(entry("decimal", 0.1));
    }

    @Test
    public void multiple_criteria_can_be_used() {

        List<Map> result = JsonPath.read(ARRAY2, "$[?(@.decimal == 0.1 && @.int == 1)]");

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).contains(entry("foo", "foo-val-1"));
    }

    @Test
    public void field_existence_can_be_checked() {

        List<Map> result = JsonPath.read(ARRAY2, "$[?(@.bool)]");

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).contains(entry("foo", "foo-val-7"));
    }

    @Test
    public void boolean_criteria_evaluates() {

        List<Map> result = JsonPath.read(ARRAY2, "$[?(@.bool == true)]");

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).contains(entry("foo", "foo-val-7"));
    }
}
