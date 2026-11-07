package com.jayway.jsonpath.old.internal;

import static com.jayway.jsonpath.internal.PathCompiler.compile;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class TestInternal3 extends TestBase {

    @Test
    public void a_root_object_can_be_evaluated() {
        Map<String, Object> result = compile("$").evaluate(DOC, DOC, CONF).getValue();

        assertThat(result).containsKey("store").hasSize(1);
    }

    @Test
    public void a_definite_array_item_property_can_be_evaluated() {

        String result = compile("$.store.book[0].author").evaluate(DOC, DOC, CONF).getValue();

        assertThat(result).isEqualTo("Nigel Rees");
    }

    @Test
    public void a_wildcard_array_item_property_can_be_evaluated() {

        List result = compile("$.store.book[*].author").evaluate(DOC, DOC, CONF).getValue();

        assertThat(result)
                .containsOnly("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien");
    }
}
