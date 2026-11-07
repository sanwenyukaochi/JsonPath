package com.jayway.jsonpath;

import com.jayway.jsonpath.spi.JsonProvider;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Filter<T> {

    public static Filter filter(Criteria criteria) {
        return new MapFilter(criteria);
    }

    public Object doFilter(Iterable<T> filterItems, Configuration configuration) {
        JsonProvider provider = configuration.getProvider();
        Object result = provider.createArray();
        for (T filterItem : filterItems) {
            if (accept(filterItem, configuration)) {
                provider.setProperty(result, provider.length(result), filterItem);
            }
        }
        return result;
    }

    public abstract boolean accept(T obj);

    public abstract boolean accept(T obj, Configuration configuration);

    public abstract Filter addCriteria(Criteria criteria);

    public abstract static class FilterAdapter<T> extends Filter<T> {

        @Override
        public boolean accept(T obj) {
            return false;
        }

        @Override
        public boolean accept(T obj, Configuration configuration) {
            return accept(obj);
        }

        @Override
        public Filter addCriteria(Criteria criteria) {
            throw new UnsupportedOperationException("can not add criteria to a FilterAdapter.");
        }
    }

    private static class MapFilter extends FilterAdapter<Map<String, Object>> {

        private HashMap<String, Criteria> criteria = new LinkedHashMap<String, Criteria>();

        public MapFilter(Criteria criteria) {
            addCriteria(criteria);
        }

        public MapFilter addCriteria(Criteria criteria) {
            Criteria existing = this.criteria.get(criteria.getKey().getPath());
            String key = criteria.getKey().getPath();
            if (existing == null) {
                this.criteria.put(key, criteria);
            } else {
                existing.andOperator(criteria);
            }
            return this;
        }

        @Override
        public boolean accept(Map<String, Object> map) {
            return accept(map, Configuration.defaultConfiguration());
        }

        @Override
        public boolean accept(Map<String, Object> map, Configuration configuration) {
            for (Criteria criterion : this.criteria.values()) {
                if (!criterion.matches(map, configuration)) {
                    return false;
                }
            }
            return true;
        }
    }
}
