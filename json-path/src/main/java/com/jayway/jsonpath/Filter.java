package com.jayway.jsonpath;

import com.jayway.jsonpath.spi.json.JsonProvider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

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

    private static class MapFilter extends FilterAdapter<Object> {

        private HashMap<String, Criteria> criteria = new LinkedHashMap<String, Criteria>();

        public MapFilter(Criteria criteria) {
            addCriteria(criteria);
        }

        public MapFilter addCriteria(Criteria criteria) {
            String key = criteria.getKey().toString();
            Criteria existing = this.criteria.get(key);
            if (existing == null) {
                this.criteria.put(key, criteria);
            } else {
                existing.andOperator(criteria);
            }
            return this;
        }

        @Override
        public boolean accept(Object map) {
            return accept(map, Configuration.defaultConfiguration());
        }

        @Override
        public boolean accept(Object map, Configuration configuration) {
            for (Criteria criterion : this.criteria.values()) {
                if (!criterion.matches(map, configuration)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[?(");

            Iterator<Criteria> criteriaIterator = criteria.values().iterator();
            Criteria criterion = criteriaIterator.next();
            sb.append(criterion.toString());

            while (criteriaIterator.hasNext()) {
                sb.append(" && ").append(criteriaIterator.next().toString());
            }
            sb.append(")]");

            return sb.toString();
        }
    }
}
