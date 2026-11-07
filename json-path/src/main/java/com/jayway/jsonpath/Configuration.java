package com.jayway.jsonpath;

import static com.jayway.jsonpath.internal.Utils.notNull;
import static java.util.Arrays.asList;

import com.jayway.jsonpath.internal.DefaultsImpl;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public class Configuration {

    private static Defaults DEFAULTS = null;

    public static synchronized void setDefaults(Defaults defaults) {
        DEFAULTS = defaults;
    }

    private static Defaults getEffectiveDefaults() {
        if (DEFAULTS == null) {
            return DefaultsImpl.INSTANCE;
        } else {
            return DEFAULTS;
        }
    }

    private final JsonProvider jsonProvider;
    private final MappingProvider mappingProvider;
    private final Set<Option> options;
    private final Collection<EvaluationListener> evaluationListeners;

    private Configuration(
            JsonProvider jsonProvider,
            MappingProvider mappingProvider,
            EnumSet<Option> options,
            Collection<EvaluationListener> evaluationListeners) {
        notNull(jsonProvider, "jsonProvider can not be null");
        notNull(mappingProvider, "mappingProvider can not be null");
        notNull(options, "setOptions can not be null");
        notNull(evaluationListeners, "evaluationListeners can not be null");
        this.jsonProvider = jsonProvider;
        this.mappingProvider = mappingProvider;
        this.options = Collections.unmodifiableSet(options);
        this.evaluationListeners = Collections.unmodifiableCollection(evaluationListeners);
    }

    public Configuration addEvaluationListeners(EvaluationListener... evaluationListener) {
        return Configuration.builder()
                .jsonProvider(jsonProvider)
                .mappingProvider(mappingProvider)
                .options(options)
                .evaluationListener(evaluationListener)
                .build();
    }

    public Configuration setEvaluationListeners(EvaluationListener... evaluationListener) {
        return Configuration.builder()
                .jsonProvider(jsonProvider)
                .mappingProvider(mappingProvider)
                .options(options)
                .evaluationListener(evaluationListener)
                .build();
    }

    public Collection<EvaluationListener> getEvaluationListeners() {
        return evaluationListeners;
    }

    public Configuration jsonProvider(JsonProvider newJsonProvider) {
        return Configuration.builder()
                .jsonProvider(newJsonProvider)
                .mappingProvider(mappingProvider)
                .options(options)
                .evaluationListener(evaluationListeners)
                .build();
    }

    public JsonProvider jsonProvider() {
        return jsonProvider;
    }

    public Configuration mappingProvider(MappingProvider newMappingProvider) {
        return Configuration.builder()
                .jsonProvider(jsonProvider)
                .mappingProvider(newMappingProvider)
                .options(options)
                .evaluationListener(evaluationListeners)
                .build();
    }

    public MappingProvider mappingProvider() {
        return mappingProvider;
    }

    public Configuration addOptions(Option... options) {
        EnumSet<Option> opts = EnumSet.noneOf(Option.class);
        opts.addAll(this.options);
        opts.addAll(asList(options));
        return Configuration.builder()
                .jsonProvider(jsonProvider)
                .mappingProvider(mappingProvider)
                .options(opts)
                .evaluationListener(evaluationListeners)
                .build();
    }

    public Configuration setOptions(Option... options) {
        return Configuration.builder()
                .jsonProvider(jsonProvider)
                .mappingProvider(mappingProvider)
                .options(options)
                .evaluationListener(evaluationListeners)
                .build();
    }

    public Set<Option> getOptions() {
        return options;
    }

    public boolean containsOption(Option option) {
        return options.contains(option);
    }

    public static Configuration defaultConfiguration() {
        Defaults defaults = getEffectiveDefaults();
        return Configuration.builder()
                .jsonProvider(defaults.jsonProvider())
                .options(defaults.options())
                .build();
    }

    public static ConfigurationBuilder builder() {
        return new ConfigurationBuilder();
    }

    public static class ConfigurationBuilder {

        private JsonProvider jsonProvider;
        private MappingProvider mappingProvider;
        private EnumSet<Option> options = EnumSet.noneOf(Option.class);
        private Collection<EvaluationListener> evaluationListener =
                new ArrayList<EvaluationListener>();

        public ConfigurationBuilder jsonProvider(JsonProvider provider) {
            this.jsonProvider = provider;
            return this;
        }

        public ConfigurationBuilder mappingProvider(MappingProvider provider) {
            this.mappingProvider = provider;
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

        public ConfigurationBuilder evaluationListener(EvaluationListener... listener) {
            this.evaluationListener = Arrays.asList(listener);
            return this;
        }

        public ConfigurationBuilder evaluationListener(Collection<EvaluationListener> listeners) {
            this.evaluationListener =
                    listeners == null ? Collections.<EvaluationListener>emptyList() : listeners;
            return this;
        }

        public Configuration build() {
            if (jsonProvider == null || mappingProvider == null) {
                final Defaults defaults = getEffectiveDefaults();
                if (jsonProvider == null) {
                    jsonProvider = defaults.jsonProvider();
                }
                if (mappingProvider == null) {
                    mappingProvider = defaults.mappingProvider();
                }
            }
            return new Configuration(jsonProvider, mappingProvider, options, evaluationListener);
        }
    }

    public interface Defaults {

        JsonProvider jsonProvider();

        Set<Option> options();

        MappingProvider mappingProvider();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Configuration that = (Configuration) o;
        return jsonProvider.getClass() == that.jsonProvider.getClass()
                && mappingProvider.getClass() == that.mappingProvider.getClass()
                && Objects.equals(options, that.options);
    }
}
