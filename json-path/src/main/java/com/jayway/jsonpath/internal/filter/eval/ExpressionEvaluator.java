package com.jayway.jsonpath.internal.filter.eval;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ExpressionEvaluator {

    public enum Operator {
        equal("=="),
        not_equal("!="),
        less_or_greater_than("<>"),
        greater_than(">"),
        greater_than_or_equal(">="),
        less_than("<"),
        less_than_or_equal("<=");

        private final String representation;

        private Operator(String representation) {
            this.representation = representation;
        }

        public String getRepresentation() {
            return representation;
        }
    }

    private static Map<String, Operator> operatorsByRepresentation;

    static {
        Map<String, Operator> map = new HashMap<String, Operator>();
        for (Operator op : Operator.values()) {
            map.put(op.getRepresentation(), op);
        }
        ExpressionEvaluator.operatorsByRepresentation = Collections.unmodifiableMap(map);
    }

    public static <T> boolean eval(T actual, String comparator, String expected) {

        Operator operator = operatorsByRepresentation.get(comparator);
        if (operator == null) {
            throw new IllegalArgumentException("Unsupported operator " + comparator);
        }

        if (actual instanceof Long) {

            Long a = (Long) actual;
            Long e = Long.parseLong(expected.trim());

            switch (operator) {
                case equal:
                    return a.longValue() == e.longValue();
                case not_equal:
                case less_or_greater_than:
                    return a.longValue() != e.longValue();
                case greater_than:
                    return a > e;
                case greater_than_or_equal:
                    return a >= e;
                case less_than:
                    return a < e;
                case less_than_or_equal:
                    return a <= e;
                default:
                    throw new UnsupportedOperationException("Cannot handle operator " + operator);
            }

        } else if (actual instanceof Integer) {
            Integer a = (Integer) actual;
            Integer e = Integer.parseInt(expected.trim());

            switch (operator) {
                case equal:
                    return a.intValue() == e.intValue();
                case not_equal:
                case less_or_greater_than:
                    return a.intValue() != e.intValue();
                case greater_than:
                    return a > e;
                case greater_than_or_equal:
                    return a >= e;
                case less_than:
                    return a < e;
                case less_than_or_equal:
                    return a <= e;
                default:
                    throw new UnsupportedOperationException("Cannot handle operator " + operator);
            }
        } else if (actual instanceof Double) {

            Double a = (Double) actual;
            Double e = Double.parseDouble(expected.trim());

            switch (operator) {
                case equal:
                    return a.doubleValue() == e.doubleValue();
                case not_equal:
                case less_or_greater_than:
                    return a.doubleValue() != e.doubleValue();
                case greater_than:
                    return a > e;
                case greater_than_or_equal:
                    return a >= e;
                case less_than:
                    return a < e;
                case less_than_or_equal:
                    return a <= e;
                default:
                    throw new UnsupportedOperationException("Cannot handle operator " + operator);
            }
        } else if (actual instanceof String) {

            switch (operator) {
                case greater_than:
                case greater_than_or_equal:
                case less_than:
                case less_than_or_equal:
                    return false;
                case equal:
                case not_equal:
                case less_or_greater_than:
                    String a = (String) actual;
                    String expectedTrimmed = expected.trim();
                    if (expectedTrimmed.startsWith("'")) {
                        expectedTrimmed = expectedTrimmed.substring(1);
                    }
                    if (expectedTrimmed.endsWith("'")) {
                        expectedTrimmed = expectedTrimmed.substring(0, expected.length() - 1);
                    }

                    if (operator == Operator.equal) {
                        return a.equals(expectedTrimmed);
                    } else if (operator == Operator.not_equal
                            || operator == Operator.less_or_greater_than) {
                        return !a.equals(expectedTrimmed);
                    }
                default:
                    throw new UnsupportedOperationException("Cannot handle operator " + operator);
            }
        } else if (actual instanceof Boolean) {
            switch (operator) {
                case equal:
                case not_equal:
                case less_or_greater_than:
                    Boolean a = (Boolean) actual;
                    Boolean e = Boolean.valueOf(expected);
                    if (operator == Operator.equal) {
                        return a.equals(e);
                    } else if (operator == Operator.not_equal
                            || operator == Operator.less_or_greater_than) {
                        return !a.equals(e);
                    }
                default:
                    throw new UnsupportedOperationException("Cannot handle operator " + operator);
            }
        }
        return false;
    }
}
