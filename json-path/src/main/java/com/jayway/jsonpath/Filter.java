package com.jayway.jsonpath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class Filter {

    public abstract boolean apply(Map<String, Object> map);

    public List<?> doFilter(List<Map<String, Object>> filterItems) {
        List<Object> result = new ArrayList<Object>();

        for (Map<String, Object> filterItem : filterItems) {

            if (apply(filterItem)) {
                result.add(filterItem);
            }
        }
        return result;
    }

    public static Filter filter(Criteria criteria) {
        return new MapFilter(criteria);
    }

    private static class MapFilter extends Filter {

        private HashMap<String, Criteria> criteria = new LinkedHashMap<String, Criteria>();

        public MapFilter(Criteria criteria) {
            addCriteria(criteria);
        }

        public MapFilter addCriteria(Criteria criteria) {
            Criteria existing = this.criteria.get(criteria.getKey());
            String key = criteria.getKey();
            if (existing == null) {
                this.criteria.put(key, criteria);
            } else {
                existing.andOperator(criteria);
            }
            return this;
        }

        protected List<Criteria> getCriteria() {
            return new ArrayList<Criteria>(this.criteria.values());
        }

        @Override
        public boolean apply(Map<String, Object> map) {

            for (Criteria criterion : getCriteria()) {
                if (!criterion.apply(map)) {
                    return false;
                }
            }

            return true;
        }
    }
}
