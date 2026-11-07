package com.jayway.jsonpath;

public interface Predicate {

    boolean apply(PredicateContext ctx);

    public interface PredicateContext {

        Object contextDocument();

        Object rootDocument();

        Configuration configuration();
    }
}
