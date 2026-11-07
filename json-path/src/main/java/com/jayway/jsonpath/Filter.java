package com.jayway.jsonpath;

import com.jayway.jsonpath.spi.JsonProvider;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class Filter<T> {

    public static Filter filter(Criteria criteria) {
        return new MapFilter(criteria);
    }

    public List doFilter(List<T> filterItems, JsonProvider jsonProvider) {
        List result = jsonProvider.createList();
        for (T filterItem : filterItems) {
            if (accept(filterItem)) {
                result.add(filterItem);
            }
        }
        return result;
    }

    public abstract boolean accept(T obj);

    public abstract Filter addCriteria(Criteria criteria);

    public abstract static class FilterAdapter<T> extends Filter<T> {

        @Override
        public boolean accept(T obj) {
            return false;
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
            for (Criteria criterion : this.criteria.values()) {
                if (!criterion.matches(map)) {
                    return false;
                }
            }
            return true;
        }
    }
}
