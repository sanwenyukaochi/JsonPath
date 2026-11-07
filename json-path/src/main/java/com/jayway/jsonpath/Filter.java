package com.jayway.jsonpath;

import static java.util.Arrays.asList;

import com.jayway.jsonpath.internal.filter.FilterCompiler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public abstract class Filter implements Predicate {

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
            String predicateString = predicate.toString();
            if (predicateString.startsWith("(")) {
                return "[?" + predicateString + "]";
            } else {
                return "[?(" + predicateString + ")]";
            }
        }
    }

    private static final class AndFilter extends Filter {

        private final Collection<Predicate> predicates;

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
            Iterator<Predicate> i = predicates.iterator();
            StringBuilder sb = new StringBuilder();
            sb.append("[?(");
            while (i.hasNext()) {
                String p = i.next().toString();

                if (p.startsWith("[?(")) {
                    p = p.substring(3, p.length() - 2);
                }
                sb.append(p);

                if (i.hasNext()) {
                    sb.append(" && ");
                }
            }
            sb.append(")]");
            return sb.toString();
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
            StringBuilder sb = new StringBuilder();
            sb.append("[?(");

            String l = left.toString();
            String r = right.toString();

            if (l.startsWith("[?(")) {
                l = l.substring(3, l.length() - 2);
            }
            if (r.startsWith("[?(")) {
                r = r.substring(3, r.length() - 2);
            }

            sb.append(l).append(" || ").append(r);

            sb.append(")]");
            return sb.toString();
        }
    }

    public static Filter parse(String filter) {
        Predicate f = FilterCompiler.compile(filter);
        return new SingleFilter(f);
    }
}
