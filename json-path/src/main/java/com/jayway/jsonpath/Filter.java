package com.jayway.jsonpath;

import static java.util.Arrays.asList;

import com.jayway.jsonpath.internal.Utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Filter implements Predicate {

    private static final Logger logger = LoggerFactory.getLogger(Filter.class);
    private static final Pattern OPERATOR_SPLIT = Pattern.compile("((?<=&&|\\|\\|)|(?=&&|\\|\\|))");
    private static final String AND = "&&";
    private static final String OR = "||";

    public static Filter filter(Predicate predicate) {
        return new SingleFilter(predicate);
    }

    public static Filter filter(Collection<Predicate> predicates) {
        return new AndFilter(predicates);
    }

    @Override
    public abstract boolean apply(PredicateContext ctx);

    public Filter or(final Predicate other) {
        return new OrFilter(this, other);
    }

    public Filter and(final Predicate other) {
        return new AndFilter(this, other);
    }

    private static final class SingleFilter extends Filter {

        private final Predicate predicate;

        private SingleFilter(Predicate predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean apply(PredicateContext ctx) {
            return predicate.apply(ctx);
        }

        @Override
        public String toString() {
            return predicate.toString();
        }
    }

    private static final class AndFilter extends Filter {

        Collection<Predicate> predicates;

        private AndFilter(Collection<Predicate> predicates) {
            this.predicates = predicates;
        }

        private AndFilter(Predicate left, Predicate right) {
            this(asList(left, right));
        }

        public Filter and(final Predicate other) {
            Collection<Predicate> newPredicates = new ArrayList<Predicate>(predicates);
            newPredicates.add(other);
            return new AndFilter(newPredicates);
        }

        @Override
        public boolean apply(PredicateContext ctx) {
            for (Predicate predicate : predicates) {
                if (!predicate.apply(ctx)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public String toString() {
            return "(" + Utils.join(" && ", predicates) + ")";
        }
    }

    private static final class OrFilter extends Filter {

        private final Predicate left;
        private final Predicate right;

        private OrFilter(Predicate left, Predicate right) {
            this.left = left;
            this.right = right;
        }

        public Filter and(final Predicate other) {
            return new OrFilter(left, new AndFilter(right, other));
        }

        @Override
        public boolean apply(PredicateContext ctx) {
            boolean a = left.apply(ctx);
            return a || right.apply(ctx);
        }

        @Override
        public String toString() {
            return "(" + left.toString() + " || " + right.toString() + ")";
        }
    }

    public static Filter parse(String filter) {
        filter = filter.trim();
        if (!filter.startsWith("[") || !filter.endsWith("]")) {
            throw new InvalidPathException(
                    "Filter must start with '[' and end with ']'. " + filter);
        }
        filter = filter.substring(1, filter.length() - 1).trim();
        if (!filter.startsWith("?")) {
            throw new InvalidPathException(
                    "Filter must start with '[?' and end with ']'. " + filter);
        }
        filter = filter.substring(1).trim();
        if (!filter.startsWith("(") || !filter.endsWith(")")) {
            throw new InvalidPathException(
                    "Filter must start with '[?(' and end with ')]'. " + filter);
        }
        filter = filter.substring(1, filter.length() - 1).trim();

        String[] split = OPERATOR_SPLIT.split(filter);
        Stack<String> operators = new Stack<String>();
        Stack<Criteria> criteria = new Stack<Criteria>();

        for (String exp : split) {
            exp = exp.trim();
            if (AND.equals(exp) || OR.equals(exp)) {
                operators.push(exp);
            } else {
                criteria.push(Criteria.parse(cleanCriteria(exp)));
            }
        }
        Filter root = new SingleFilter(criteria.pop());
        while (!operators.isEmpty()) {
            String operator = operators.pop();
            if (AND.equals(operator)) {
                root = root.and(criteria.pop());

            } else {
                if (criteria.isEmpty()) {
                    throw new InvalidPathException("Invalid operators " + filter);
                }
                root = root.or(criteria.pop());
            }
        }
        if (!operators.isEmpty() || !criteria.isEmpty()) {
            throw new InvalidPathException("Invalid operators " + filter);
        }

        if (logger.isDebugEnabled()) logger.debug("Parsed filter: " + root.toString());
        return root;
    }

    private static String cleanCriteria(String filter) {
        int begin = 0;
        int end = filter.length() - 1;

        char c = filter.charAt(begin);
        while (c == '[' || c == '?' || c == '(' || c == ' ') {
            c = filter.charAt(++begin);
        }

        c = filter.charAt(end);
        while (c == ')' || c == ' ') {
            c = filter.charAt(--end);
        }
        return filter.substring(begin, end + 1);
    }
}
