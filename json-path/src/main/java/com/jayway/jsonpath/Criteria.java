package com.jayway.jsonpath;

import static com.jayway.jsonpath.internal.Utils.isNumeric;
import static com.jayway.jsonpath.internal.Utils.notNull;
import static java.util.Arrays.asList;

import com.jayway.jsonpath.internal.Utils;
import com.jayway.jsonpath.internal.spi.compiler.PathCompiler;
import com.jayway.jsonpath.spi.compiler.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

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

    private final Path key;

    private final List<Criteria> criteriaChain;

    private final LinkedHashMap<CriteriaType, Object> criteria =
            new LinkedHashMap<CriteriaType, Object>();

    private Object isValue = NOT_SET;

    private Criteria(String key) {
        Utils.notEmpty(key, "key can not be null or empty");
        this.criteriaChain = new ArrayList<Criteria>();
        this.criteriaChain.add(this);
        this.key = PathCompiler.tokenize(key);
    }

    private Criteria(Path path) {
        Utils.notNull(path, "path can not be null");
        this.criteriaChain = new ArrayList<Criteria>();
        this.criteriaChain.add(this);
        this.key = path;
    }

    private Criteria(List<Criteria> criteriaChain, String key) {
        Utils.notEmpty(key, "key can not be null or empty");
        this.criteriaChain = criteriaChain;

        this.criteriaChain.add(this);

        this.key = PathCompiler.tokenize(key);
    }

    Path getKey() {
        return this.key;
    }

    boolean matches(Object map, Configuration configuration) {
        for (Criteria c : this.criteriaChain) {
            if (!c.singleObjectApply(map, configuration)) {
                return false;
            }
        }
        return true;
    }

    private static Object readSafely(Path path, Object map) {
        try {
            return path.evaluate(map, Configuration.defaultConfiguration()).get();

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

    private boolean singleObjectApply(Object map, final Configuration configuration) {

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
                if (configuration.getProvider().isArray(map)) {
                    return false;
                }
                final boolean exp = (Boolean) expectedVal;
                return objectOrAnyCollectionItemMatches(
                        map,
                        new Predicate<Object>() {
                            @Override
                            public boolean accept(final Object value) {
                                boolean act = true;
                                Object res;
                                try {
                                    Set<Option> options =
                                            new HashSet<Option>() {
                                                {
                                                    add(Option.THROW_ON_MISSING_PROPERTY);
                                                }
                                            };

                                    res =
                                            getKey().evaluate(
                                                            value,
                                                            Configuration.defaultConfiguration()
                                                                    .options(
                                                                            Option
                                                                                    .THROW_ON_MISSING_PROPERTY))
                                                    .get();
                                    if (configuration.getProvider().isArray(res)) {

                                        if (getKey().isDefinite()) {
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

    public static Criteria where(Path key) {
        return new Criteria(key);
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

    public Criteria size(int size) {
        checkFilterCanBeApplied(CriteriaType.SIZE);
        criteria.put(CriteriaType.SIZE, size);
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

    public Criteria matches(String operator, String expected) {
        Object check;

        if (expected.startsWith("'") && expected.endsWith("'")) {
            check = expected.substring(1, expected.length() - 1);
        } else if ("true".equals(expected)) {
            check = Boolean.TRUE;
        } else if ("false".equals(expected)) {
            check = Boolean.FALSE;
        } else if ("null".equals(expected)) {
            check = null;
        } else if (isNumeric(expected)) {
            if (expected.contains(".")) {
                check = Double.parseDouble(expected);
            } else {
                check = Integer.parseInt(expected);
            }
        } else {
            throw new UnsupportedOperationException("Type not supported: " + expected);
        }
        if ("==".equals(operator)) {
            return is(check);
        } else if (">".equals(operator)) {
            return gt(check);
        } else if (">=".equals(operator)) {
            return gte(check);
        } else if ("<".equals(operator)) {
            return lt(check);
        } else if ("<=".equals(operator)) {
            return lte(check);
        } else if ("!=".equals(operator)) {
            return ne(check);
        } else if ("<>".equals(operator)) {
            return ne(check);
        } else {
            throw new UnsupportedOperationException("Operator not supported: " + operator);
        }
    }

    public Criteria andOperator(Criteria... criteria) {
        criteriaChain.add(new Criteria("$and").is(asList(criteria)));
        return this;
    }

    private void checkFilterCanBeApplied(CriteriaType type) {

        if (getKey().tokenCount() > 2) {
            throw new IllegalArgumentException(
                    "Cannot use " + type + " filter on a multi-level path expression");
        }
    }

    private interface Predicate<T> {
        boolean accept(T value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        Iterator<Criteria> iterator = criteriaChain.iterator();
        Criteria c = iterator.next();

        if (c.isValue != NOT_SET) {
            sb.append("@")
                    .append(
                            key.toString().substring(1)
                                    + " == "
                                    + ((c.isValue instanceof String)
                                            ? ("'" + c.isValue + "'")
                                            : c.isValue.toString()));
        }
        return sb.toString();
    }
}
