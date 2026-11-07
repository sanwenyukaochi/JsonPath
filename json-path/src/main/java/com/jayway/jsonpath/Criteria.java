package com.jayway.jsonpath;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.lang3.Validate;

public class Criteria {

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
        NOT_EMPTY,
        OR
    }

    private static final Object NOT_SET = new Object();

    private final JsonPath key;

    private final List<Criteria> criteriaChain;

    private final LinkedHashMap<CriteriaType, Object> criteria =
            new LinkedHashMap<CriteriaType, Object>();

    private Object isValue = NOT_SET;

    private Criteria(String key) {
        Validate.notEmpty(key, "key can not be null or empty");
        this.criteriaChain = new ArrayList<Criteria>();
        this.criteriaChain.add(this);
        this.key = JsonPath.compile(key);
    }

    private Criteria(List<Criteria> criteriaChain, String key) {
        Validate.notEmpty(key, "key can not be null or empty");
        this.criteriaChain = criteriaChain;
        this.criteriaChain.add(this);
        this.key = JsonPath.compile(key);
    }

    JsonPath getKey() {
        return this.key;
    }

    boolean matches(Map<String, Object> map, Configuration configuration) {

        if (this.criteriaChain.size() == 1) {
            return criteriaChain.get(0).singleObjectApply(map, configuration);
        } else {
            for (Criteria c : this.criteriaChain) {
                if (!c.singleObjectApply(map, configuration)) {
                    return false;
                }
            }
            return true;
        }
    }

    private static Object readSafely(JsonPath path, Map<String, Object> map) {
        try {
            return path.read(map);
        } catch (InvalidPathException e) {
            return null;
        }
    }

    private static <T> boolean objectOrAnyCollectionItemMatches(
            final Object singleObjectOrCollection, final Predicate<T> predicate) {
        if (singleObjectOrCollection instanceof Collection) {
            Iterator it = ((Collection) singleObjectOrCollection).iterator();
            while (it.hasNext()) {
                if (predicate.accept((T) it.next())) {
                    return true;
                }
            }
            return false;
        }
        return predicate.accept((T) singleObjectOrCollection);
    }

    boolean singleObjectApply(Map<String, Object> map, final Configuration configuration) {

        for (CriteriaType key : this.criteria.keySet()) {

            final Object actualVal = readSafely(this.key, map);
            final Object expectedVal = this.criteria.get(key);

            if (CriteriaType.GT.equals(key)) {

                if (expectedVal == null || actualVal == null) {
                    return false;
                }

                final Number expectedNumber = (Number) expectedVal;
                return objectOrAnyCollectionItemMatches(
                        actualVal,
                        new Predicate<Number>() {

                            @Override
                            public boolean accept(Number value) {
                                return (value.doubleValue() > expectedNumber.doubleValue());
                            }
                        });

            } else if (CriteriaType.GTE.equals(key)) {

                if (expectedVal == null || actualVal == null) {
                    return false;
                }

                final Number expectedNumber = (Number) expectedVal;
                return objectOrAnyCollectionItemMatches(
                        actualVal,
                        new Predicate<Number>() {

                            @Override
                            public boolean accept(Number value) {
                                return (value.doubleValue() >= expectedNumber.doubleValue());
                            }
                        });

            } else if (CriteriaType.LT.equals(key)) {

                if (expectedVal == null || actualVal == null) {
                    return false;
                }

                final Number expectedNumber = (Number) expectedVal;
                return objectOrAnyCollectionItemMatches(
                        actualVal,
                        new Predicate<Number>() {

                            @Override
                            public boolean accept(Number value) {
                                return (value.doubleValue() < expectedNumber.doubleValue());
                            }
                        });

            } else if (CriteriaType.LTE.equals(key)) {

                if (expectedVal == null || actualVal == null) {
                    return false;
                }

                final Number expectedNumber = (Number) expectedVal;
                return objectOrAnyCollectionItemMatches(
                        actualVal,
                        new Predicate<Number>() {

                            @Override
                            public boolean accept(Number value) {
                                return (value.doubleValue() <= expectedNumber.doubleValue());
                            }
                        });

            } else if (CriteriaType.NE.equals(key)) {

                return objectOrAnyCollectionItemMatches(
                        actualVal,
                        new Predicate<Object>() {

                            @Override
                            public boolean accept(Object value) {
                                if (expectedVal == null && value == null) {
                                    return false;
                                }
                                if (expectedVal == null) {
                                    return true;
                                } else {
                                    return !expectedVal.equals(value);
                                }
                            }
                        });

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

            } else if (CriteriaType.NOT_EMPTY.equals(key)) {

                if (actualVal == null) {
                    return false;
                }
                boolean empty = false;
                if (actualVal instanceof Collection) {
                    empty = ((List) actualVal).isEmpty();
                } else if (actualVal instanceof String) {
                    empty = ((String) actualVal).isEmpty();
                }

                return !empty;

            } else if (CriteriaType.EXISTS.equals(key)) {

                final boolean exp = (Boolean) expectedVal;
                return objectOrAnyCollectionItemMatches(
                        map,
                        new Predicate<Object>() {
                            @Override
                            public boolean accept(final Object value) {
                                boolean act = true;
                                Object res;
                                try {
                                    res =
                                            getKey().read(
                                                            value,
                                                            configuration.options(
                                                                    Option
                                                                            .THROW_ON_MISSING_PROPERTY));

                                    if (configuration.getProvider().isArray(res)) {
                                        if (getKey().isPathDefinite()) {
                                            act = true;
                                        } else {
                                            act = (configuration.getProvider().length(res) > 0);
                                        }
                                    }
                                } catch (InvalidPathException e) {
                                    act = false;
                                }
                                return act == exp;
                            }
                        });

            } else if (CriteriaType.TYPE.equals(key)) {

                final Class<?> exp = (Class<?>) expectedVal;
                return objectOrAnyCollectionItemMatches(
                        actualVal,
                        new Predicate<Object>() {

                            @Override
                            public boolean accept(Object value) {
                                Class<?> act = value == null ? null : value.getClass();
                                if (act == null) {
                                    return false;
                                } else {
                                    return act.equals(exp);
                                }
                            }
                        });

            } else if (CriteriaType.REGEX.equals(key)) {
                final Pattern exp = (Pattern) expectedVal;

                return objectOrAnyCollectionItemMatches(
                        actualVal,
                        new Predicate<String>() {

                            @Override
                            public boolean accept(String value) {
                                return value != null && exp.matcher(value).matches();
                            }
                        });

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
                        if (!c.singleObjectApply(map, configuration)) {
                            return false;
                        }
                    }
                }
                return true;
            } else {
                Object actualVal = readSafely(this.key, map);
                return objectOrAnyCollectionItemMatches(
                        actualVal,
                        new Predicate<Object>() {
                            @Override
                            public boolean accept(Object value) {
                                if (isValue == null) {
                                    return value == null;
                                } else {
                                    return isValue.equals(value);
                                }
                            }
                        });
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
        checkFilterCanBeApplied(CriteriaType.IN);
        criteria.put(CriteriaType.IN, c);
        return this;
    }

    public Criteria nin(Object... o) {
        return nin(Arrays.asList(o));
    }

    public Criteria nin(Collection<?> c) {
        notNull(c, "collection can not be null");
        checkFilterCanBeApplied(CriteriaType.NIN);
        criteria.put(CriteriaType.NIN, c);
        return this;
    }

    public Criteria all(Object... o) {
        return all(Arrays.asList(o));
    }

    public Criteria all(Collection<?> c) {
        notNull(c, "collection can not be null");
        checkFilterCanBeApplied(CriteriaType.ALL);
        criteria.put(CriteriaType.ALL, c);
        return this;
    }

    public Criteria size(int s) {
        checkFilterCanBeApplied(CriteriaType.SIZE);
        criteria.put(CriteriaType.SIZE, s);
        return this;
    }

    public Criteria notEmpty() {
        checkFilterCanBeApplied(CriteriaType.NOT_EMPTY);
        criteria.put(CriteriaType.NOT_EMPTY, null);
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

    private void checkFilterCanBeApplied(CriteriaType type) {
        if (getKey().getTokenizer().size() > 2) {
            throw new IllegalArgumentException(
                    "Cannot use " + type + " filter on a multi-level path expression");
        }
    }

    private interface Predicate<T> {
        boolean accept(T value);
    }
}
