package com.jayway.jsonpath;

import static com.jayway.jsonpath.internal.Utils.notNull;
import static java.util.Arrays.asList;

import com.jayway.jsonpath.internal.spi.json.JsonSmartJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class Configuration {

    private static Defaults DEFAULTS =
            new Defaults() {
                @Override
                public JsonProvider provider() {
                    return new JsonSmartJsonProvider();
                }

                @Override
                public Set<Option> options() {
                    return EnumSet.noneOf(Option.class);
                }
            };

    public static synchronized void setDefaults(Defaults defaults) {
        DEFAULTS = defaults;
    }

    private final JsonProvider provider;
    private final Set<Option> options;

    private Configuration(JsonProvider provider, EnumSet<Option> options) {
        notNull(provider, "provider can not be null");
        notNull(options, "options can not be null");
        this.provider = provider;
        this.options = Collections.unmodifiableSet(options);
    }

    public Configuration provider(JsonProvider provider) {
        return Configuration.builder().jsonProvider(provider).options(options).build();
    }

    public JsonProvider getProvider() {
        return provider;
    }

    public Configuration addOptions(Option... options) {
        EnumSet<Option> opts = EnumSet.noneOf(Option.class);
        opts.addAll(this.options);
        opts.addAll(asList(options));
        return Configuration.builder().jsonProvider(provider).options(opts).build();
    }

    public Configuration options(Option... options) {
        return Configuration.builder().jsonProvider(provider).options(options).build();
    }

    public Set<Option> getOptions() {
        return options;
    }

    public boolean containsOption(Option option) {
        return options.contains(option);
    }

    public static Configuration defaultConfiguration() {
        return Configuration.builder()
                .jsonProvider(DEFAULTS.provider())
                .options(DEFAULTS.options())
                .build();
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
            if (flags.length > 0) {
                this.options.addAll(asList(flags));
            }
            return this;
        }

        public ConfigurationBuilder options(Set<Option> options) {
            this.options.addAll(options);
            return this;
        }

        public Configuration build() {
            if (provider == null) {
                provider = DEFAULTS.provider();
            }
            return new Configuration(provider, options);
        }
    }

    public interface Defaults {

        JsonProvider provider();

        Set<Option> options();
    }
}
