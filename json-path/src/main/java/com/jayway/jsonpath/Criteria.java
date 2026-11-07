package com.jayway.jsonpath;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Criteria {

    private static final Object NOT_SET = new Object();

    private enum CriteriaType {
        GT,
        GTE,
        LT,
        LTE,
        NE,
        IN,
        NIN,
        ALL,
        SIZE,
        EXISTS,
        TYPE,
        REGEX,
        OR
    }

    private final String key;

    private final List<Criteria> criteriaChain;

    private final LinkedHashMap<CriteriaType, Object> criteria =
            new LinkedHashMap<CriteriaType, Object>();

    private Object isValue = NOT_SET;

    private Criteria(String key) {
        notEmpty(key, "key can not be null or empty");
        this.criteriaChain = new ArrayList<Criteria>();
        this.criteriaChain.add(this);
        this.key = key;
    }

    private Criteria(List<Criteria> criteriaChain, String key) {
        notEmpty(key, "key can not be null or empty");
        this.criteriaChain = criteriaChain;
        this.criteriaChain.add(this);
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public boolean matches(Map<String, Object> map) {

        if (this.criteriaChain.size() == 1) {
            return criteriaChain.get(0).singleObjectApply(map);
        } else {
            for (Criteria c : this.criteriaChain) {

                System.out.println("");
                if (!c.singleObjectApply(map)) {
                    return false;
                }
            }
            return true;
        }
    }

    boolean singleObjectApply(Map<String, Object> map) {

        for (CriteriaType key : this.criteria.keySet()) {

            Object actualVal = map.get(this.key);
            Object expectedVal = this.criteria.get(key);

            if (CriteriaType.GT.equals(key)) {

                if (expectedVal == null || actualVal == null) {
                    return false;
                }

                Number expectedNumber = (Number) expectedVal;
                Number actualNumber = (Number) actualVal;

                return (actualNumber.doubleValue() > expectedNumber.doubleValue());

            } else if (CriteriaType.GTE.equals(key)) {

                if (expectedVal == null || actualVal == null) {
                    return false;
                }

                Number expectedNumber = (Number) expectedVal;
                Number actualNumber = (Number) actualVal;

                return (actualNumber.doubleValue() >= expectedNumber.doubleValue());

            } else if (CriteriaType.LT.equals(key)) {

                if (expectedVal == null || actualVal == null) {
                    return false;
                }

                Number expectedNumber = (Number) expectedVal;
                Number actualNumber = (Number) actualVal;

                return (actualNumber.doubleValue() < expectedNumber.doubleValue());

            } else if (CriteriaType.LTE.equals(key)) {

                if (expectedVal == null || actualVal == null) {
                    return false;
                }

                Number expectedNumber = (Number) expectedVal;
                Number actualNumber = (Number) actualVal;

                return (actualNumber.doubleValue() <= expectedNumber.doubleValue());

            } else if (CriteriaType.NE.equals(key)) {
                if (expectedVal == null && actualVal == null) {
                    return false;
                }
                if (expectedVal == null) {
                    return true;
                } else {
                    return !expectedVal.equals(actualVal);
                }

            } else if (CriteriaType.IN.equals(key)) {

                Collection exp = (Collection) expectedVal;

                return exp.contains(actualVal);

            } else if (CriteriaType.NIN.equals(key)) {

                Collection exp = (Collection) expectedVal;

                return !exp.contains(actualVal);
            } else if (CriteriaType.ALL.equals(key)) {

                Collection exp = (Collection) expectedVal;
                Collection act = (Collection) actualVal;

                return act.containsAll(exp);

            } else if (CriteriaType.SIZE.equals(key)) {

                int exp = (Integer) expectedVal;
                List act = (List) actualVal;

                return (act.size() == exp);

            } else if (CriteriaType.EXISTS.equals(key)) {

                boolean exp = (Boolean) expectedVal;
                boolean act = map.containsKey(this.key);

                return act == exp;

            } else if (CriteriaType.TYPE.equals(key)) {

                Class<?> exp = (Class<?>) expectedVal;
                Class<?> act = null;
                if (map.containsKey(this.key)) {
                    Object actVal = map.get(this.key);
                    if (actVal != null) {
                        act = actVal.getClass();
                    }
                }
                if (act == null) {
                    return false;
                } else {
                    return act.equals(exp);
                }

            } else if (CriteriaType.REGEX.equals(key)) {

                Pattern exp = (Pattern) expectedVal;
                String act = (String) actualVal;
                if (act == null) {
                    return false;
                }
                return exp.matcher(act).matches();

            } else {
                throw new UnsupportedOperationException(
                        "Criteria type not supported: " + key.name());
            }
        }
        if (isValue != NOT_SET) {

            if (isValue instanceof Collection) {
                Collection<Criteria> cs = (Collection<Criteria>) isValue;
                for (Criteria crit : cs) {
                    for (Criteria c : crit.criteriaChain) {
                        if (!c.singleObjectApply(map)) {
                            return false;
                        }
                    }
                }
                return true;
            } else {

                if (isValue == null) {
                    return (map.get(key) == null);
                } else {
                    return isValue.equals(map.get(key));
                }
            }
        } else {

        }
        return true;
    }

    public static Criteria where(String key) {
        return new Criteria(key);
    }

    public Criteria and(String key) {
        return new Criteria(this.criteriaChain, key);
    }

    public Criteria is(Object o) {
        if (isValue != NOT_SET) {
            throw new InvalidCriteriaException(
                    "Multiple 'is' values declared. You need to use 'and' with multiple criteria");
        }
        if (this.criteria.size() > 0
                && "$not".equals(this.criteria.keySet().toArray()[this.criteria.size() - 1])) {
            throw new InvalidCriteriaException(
                    "Invalid query: 'not' can't be used with 'is' - use 'ne' instead.");
        }
        this.isValue = o;
        return this;
    }

    public Criteria eq(Object o) {
        return is(o);
    }

    public Criteria ne(Object o) {
        criteria.put(CriteriaType.NE, o);
        return this;
    }

    public Criteria lt(Object o) {
        criteria.put(CriteriaType.LT, o);
        return this;
    }

    public Criteria lte(Object o) {
        criteria.put(CriteriaType.LTE, o);
        return this;
    }

    public Criteria gt(Object o) {
        criteria.put(CriteriaType.GT, o);
        return this;
    }

    public Criteria gte(Object o) {
        criteria.put(CriteriaType.GTE, o);
        return this;
    }

    public Criteria in(Object... o) {
        if (o.length > 1 && o[1] instanceof Collection) {
            throw new InvalidCriteriaException(
                    "You can only pass in one argument of type " + o[1].getClass().getName());
        }
        return in(Arrays.asList(o));
    }

    public Criteria in(Collection<?> c) {
        notNull(c, "collection can not be null");
        criteria.put(CriteriaType.IN, c);
        return this;
    }

    public Criteria nin(Object... o) {
        return nin(Arrays.asList(o));
    }

    public Criteria nin(Collection<?> c) {
        notNull(c, "collection can not be null");
        criteria.put(CriteriaType.NIN, c);
        return this;
    }

    public Criteria all(Object... o) {
        return all(Arrays.asList(o));
    }

    public Criteria all(Collection<?> c) {
        notNull(c, "collection can not be null");
        criteria.put(CriteriaType.ALL, c);
        return this;
    }

    public Criteria size(int s) {
        criteria.put(CriteriaType.SIZE, s);
        return this;
    }

    public Criteria exists(boolean b) {
        criteria.put(CriteriaType.EXISTS, b);
        return this;
    }

    public Criteria type(Class<?> t) {
        notNull(t, "type can not be null");
        criteria.put(CriteriaType.TYPE, t);
        return this;
    }

    public Criteria regex(Pattern pattern) {
        notNull(pattern, "pattern can not be null");
        criteria.put(CriteriaType.REGEX, pattern);
        return this;
    }

    public Criteria andOperator(Criteria... criteria) {
        criteriaChain.add(new Criteria("$and").is(asList(criteria)));
        return this;
    }
}
