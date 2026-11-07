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

    private String key;

    private List<Criteria> criteriaChain;

    private LinkedHashMap<String, Object> criteria = new LinkedHashMap<String, Object>();

    private Object isValue = NOT_SET;

    public Criteria(String key) {
        notEmpty(key, "key can not be null or empty");
        this.criteriaChain = new ArrayList<Criteria>();
        this.criteriaChain.add(this);
        this.key = key;
    }

    protected Criteria(List<Criteria> criteriaChain, String key) {
        notEmpty(key, "key can not be null or empty");
        this.criteriaChain = criteriaChain;
        this.criteriaChain.add(this);
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public boolean apply(Map<String, Object> map) {

        if (this.criteriaChain.size() == 1) {
            return criteriaChain.get(0).singleObjectApply(map);
        } else {

            for (Criteria c : this.criteriaChain) {

                if (!c.singleObjectApply(map)) {
                    return false;
                }
            }
            return true;
        }
    }

    protected boolean singleObjectApply(Map<String, Object> map) {

        boolean not = false;
        for (String key : this.criteria.keySet()) {
            Object expectedVal = null;
            Object actualVal = map.get(this.key);
            if (not) {
                expectedVal = this.criteria.get(key);
                not = false;
            } else {
                if ("$not".equals(key)) {
                    not = true;
                } else {
                    expectedVal = this.criteria.get(key);
                    if ("$gt".equals(key)) {

                        if (expectedVal == null || actualVal == null) {
                            return false;
                        }

                        Number expectedNumber = (Number) expectedVal;
                        Number actualNumber = (Number) actualVal;

                        return (actualNumber.doubleValue() > expectedNumber.doubleValue());

                    } else if ("$gte".equals(key)) {

                        if (expectedVal == null || actualVal == null) {
                            return false;
                        }

                        Number expectedNumber = (Number) expectedVal;
                        Number actualNumber = (Number) actualVal;

                        return (actualNumber.doubleValue() >= expectedNumber.doubleValue());

                    } else if ("$lt".equals(key)) {

                        if (expectedVal == null || actualVal == null) {
                            return false;
                        }

                        Number expectedNumber = (Number) expectedVal;
                        Number actualNumber = (Number) actualVal;

                        return (actualNumber.doubleValue() < expectedNumber.doubleValue());

                    } else if ("$lte".equals(key)) {

                        if (expectedVal == null || actualVal == null) {
                            return false;
                        }

                        Number expectedNumber = (Number) expectedVal;
                        Number actualNumber = (Number) actualVal;

                        return (actualNumber.doubleValue() <= expectedNumber.doubleValue());

                    } else if ("$ne".equals(key)) {
                        if (expectedVal == null && actualVal == null) {
                            return false;
                        }
                        if (expectedVal == null) {
                            return !(actualVal == null);
                        }

                        return !expectedVal.equals(actualVal);

                    } else if ("$in".equals(key)) {

                        Collection exp = (Collection) expectedVal;

                        return exp.contains(actualVal);

                    } else if ("$nin".equals(key)) {

                        Collection exp = (Collection) expectedVal;

                        return !exp.contains(actualVal);
                    } else if ("$all".equals(key)) {

                        Collection exp = (Collection) expectedVal;
                        Collection act = (Collection) actualVal;

                        return act.containsAll(exp);

                    } else if ("$size".equals(key)) {

                        int exp = (Integer) expectedVal;
                        List act = (List) actualVal;

                        return (act.size() == exp);

                    } else if ("$exists".equals(key)) {

                        boolean exp = (Boolean) expectedVal;
                        boolean act = map.containsKey(this.key);

                        return act == exp;

                    } else if ("$type".equals(key)) {

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

                    } else if ("$regex".equals(key)) {

                        Pattern exp = (Pattern) expectedVal;
                        String act = (String) actualVal;
                        if (act == null) {
                            return false;
                        }
                        return exp.matcher(act).matches();

                    } else if ("$or".equals(key)) {

                        throw new UnsupportedOperationException("Or not supported yet");
                    }
                }
            }
        }
        if (isValue != NOT_SET) {

            if (isValue == null) {
                return (map.get(key) == null);
            } else {
                return isValue.equals(map.get(key));
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
        criteria.put("$ne", o);
        return this;
    }

    public Criteria lt(Object o) {
        criteria.put("$lt", o);
        return this;
    }

    public Criteria lte(Object o) {
        criteria.put("$lte", o);
        return this;
    }

    public Criteria gt(Object o) {
        criteria.put("$gt", o);
        return this;
    }

    public Criteria gte(Object o) {
        criteria.put("$gte", o);
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
        criteria.put("$in", c);
        return this;
    }

    public Criteria nin(Object... o) {
        return nin(Arrays.asList(o));
    }

    public Criteria nin(Collection<?> o) {
        notNull(o, "collection can not be null");
        criteria.put("$nin", o);
        return this;
    }

    public Criteria all(Object... o) {
        return all(Arrays.asList(o));
    }

    public Criteria all(Collection<?> o) {
        criteria.put("$all", o);
        return this;
    }

    public Criteria size(int s) {
        criteria.put("$size", s);
        return this;
    }

    public Criteria exists(boolean b) {
        criteria.put("$exists", b);
        return this;
    }

    public Criteria type(Class<?> t) {
        criteria.put("$type", t);
        return this;
    }

    public Criteria not() {
        criteria.put("$not", null);
        return this;
    }

    public Criteria regex(Pattern pattern) {
        criteria.put("$regex", pattern);

        return this;
    }

    public Criteria orOperator(Criteria... criteria) {
        criteriaChain.add(new Criteria("$or").is(asList(criteria)));
        return this;
    }

    public Criteria andOperator(Criteria... criteria) {
        criteriaChain.add(new Criteria("$and").is(asList(criteria)));
        return this;
    }
}
