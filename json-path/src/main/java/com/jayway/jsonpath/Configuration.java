package com.jayway.jsonpath;

import static com.jayway.jsonpath.internal.Utils.notNull;

import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class Configuration {

    private final JsonProvider provider;
    private final EnumSet<Option> options;

    private Configuration(JsonProvider provider, EnumSet<Option> options) {
        notNull(provider, "provider can not be null");
        notNull(options, "options can not be null");
        this.provider = provider;
        this.options = options;
    }

    public Configuration provider(JsonProvider provider) {
        return Configuration.builder().jsonProvider(provider).options(options).build();
    }

    public JsonProvider getProvider() {
        return provider;
    }

    public Configuration options(Option... options) {
        return Configuration.builder().jsonProvider(provider).options(options).build();
    }

    public Set<Option> getOptions() {
        return Collections.unmodifiableSet(options);
    }

    public static Configuration defaultConfiguration() {
        return new Configuration(
                JsonProviderFactory.createProvider(), EnumSet.noneOf(Option.class));
    }

    public static ConfigurationBuilder builder() {
        return new ConfigurationBuilder();
    }

    public static class ConfigurationBuilder {

        private JsonProvider provider;
        private EnumSet<Option> options = EnumSet.noneOf(Option.class);

        public ConfigurationBuilder jsonProvider(JsonProvider provider) {
            this.provider = provider;
            return this;
        }

        public ConfigurationBuilder options(Option... flags) {
            this.options.addAll(Arrays.asList(flags));
            return this;
        }

        public ConfigurationBuilder options(Set<Option> options) {
            this.options.addAll(options);
            return this;
        }

        public Configuration build() {
            if (provider == null) {
                provider = JsonProviderFactory.createProvider();
            }
            return new Configuration(provider, options);
        }
    }
}
