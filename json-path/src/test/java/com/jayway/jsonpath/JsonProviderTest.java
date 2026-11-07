package com.jayway.jsonpath;

import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.MethodSource;

@ParameterizedClass
@MethodSource("configurations")
public class JsonProviderTest extends BaseTest {

    private final Configuration conf;

    public JsonProviderTest(Configuration conf) {
        this.conf = conf;
    }

    public static Iterable<Configuration> configurations() {
        return Configurations.configurations();
    }

    @Test
    public void strings_are_unwrapped() {
        assertThat(using(conf).parse(JSON_DOCUMENT).read("$.string-property", String.class))
                .isEqualTo("string-value");
    }

    @Test
    public void integers_are_unwrapped() {
        assertThat(using(conf).parse(JSON_DOCUMENT).read("$.int-max-property", Integer.class))
                .isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void ints_are_unwrapped() {
        assertThat(using(conf).parse(JSON_DOCUMENT).read("$.int-max-property", int.class))
                .isEqualTo(Integer.MAX_VALUE);
    }
}
